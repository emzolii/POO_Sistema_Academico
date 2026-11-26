package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelo.Aluno;
import modelo.Disciplina;
import modelo.Matricula;
import modelo.MatriculaDuplicadaException;
import modelo.SalaSemCapacidadeException;

public class MatriculaDAO {
    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

    // RF3.4 + Entrega 3: Matrícula e vagas gravadas juntas em uma única transação
    public void matricular(Matricula matricula) {
        if (matricula.getAluno() == null || matricula.getAluno().getId() <= 0) {
            throw new IllegalArgumentException("O aluno precisa estar salvo no banco antes de ser matriculado.");
        }
        if (matricula.getDisciplina() == null) {
            throw new IllegalArgumentException("Disciplina inválida para matrícula.");
        }

        int idAluno = matricula.getAluno().getId();
        int codigoDisciplina = matricula.getDisciplina().getCodigo();
        String semestre = matricula.getSemestre();

        String sqlDuplicada = "SELECT 1 FROM matricula WHERE id_aluno = ? AND codigo_disciplina = ? AND semestre = ?";
        String sqlChecarDisciplina = """
            SELECT d.vagas, d.vagas_disponiveis, d.id_sala, s.capacidade
            FROM disciplina d
            LEFT JOIN sala s ON d.id_sala = s.id
            WHERE d.codigo = ?
        """;
        String sqlInserir = """
            INSERT INTO matricula (id_aluno, codigo_disciplina, semestre, nota01, nota02, nota03, nota04, media, situacao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        String sqlDescontarVaga = "UPDATE disciplina SET vagas_disponiveis = vagas_disponiveis - 1 WHERE codigo = ?";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false); // Inicia transação

            try {
                // 1. RN3.1: Impedir matrícula repetida na mesma disciplina e semestre
                try (PreparedStatement psDup = conn.prepareStatement(sqlDuplicada)) {
                    psDup.setInt(1, idAluno);
                    psDup.setInt(2, codigoDisciplina);
                    psDup.setString(3, semestre);
                    try (ResultSet rs = psDup.executeQuery()) {
                        if (rs.next()) {
                            throw new MatriculaDuplicadaException(
                                "Aluno já matriculado nesta disciplina no semestre " + semestre
                            );
                        }
                    }
                }

                // 2. RN3.2: Validar sala definida, capacidade da sala e vagas disponíveis
                int vagasDisponiveis;
                try (PreparedStatement psDisc = conn.prepareStatement(sqlChecarDisciplina)) {
                    psDisc.setInt(1, codigoDisciplina);
                    try (ResultSet rs = psDisc.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Disciplina não encontrada no banco: " + codigoDisciplina);
                        }
                        if (rs.getObject("id_sala") == null) {
                            throw new SalaSemCapacidadeException("Disciplina não possui sala definida.");
                        }

                        int vagas = rs.getInt("vagas");
                        vagasDisponiveis = rs.getInt("vagas_disponiveis");
                        int capacidadeSala = rs.getInt("capacidade");

                        if (vagas > capacidadeSala) {
                            throw new SalaSemCapacidadeException("Número de vagas excede a capacidade da sala.");
                        }
                        if (vagasDisponiveis <= 0) {
                            throw new SalaSemCapacidadeException("Não há vagas disponíveis nesta disciplina.");
                        }
                    }
                }

                // 3. Gravar matrícula
                try (PreparedStatement psIns = conn.prepareStatement(sqlInserir, Statement.RETURN_GENERATED_KEYS)) {
                    psIns.setInt(1, idAluno);
                    psIns.setInt(2, codigoDisciplina);
                    psIns.setString(3, semestre);
                    psIns.setDouble(4, matricula.getNota01());
                    psIns.setDouble(5, matricula.getNota02());
                    psIns.setDouble(6, matricula.getNota03());
                    psIns.setDouble(7, matricula.getNota04());
                    psIns.setDouble(8, matricula.getMedia());
                    psIns.setString(9, matricula.getSituacao());
                    psIns.executeUpdate();

                    try (ResultSet rs = psIns.getGeneratedKeys()) {
                        if (rs.next()) {
                            matricula.setId(rs.getInt(1));
                        }
                    }
                }

                // 4. Atualizar vagas disponíveis na mesma transação
                try (PreparedStatement psVaga = conn.prepareStatement(sqlDescontarVaga)) {
                    psVaga.setInt(1, codigoDisciplina);
                    psVaga.executeUpdate();
                }

                conn.commit(); // Confirma matrícula e desconto de vaga juntos
                matricula.getDisciplina().setVagasDisponiveis(vagasDisponiveis - 1);

            } catch (RuntimeException e) {
                conn.rollback();
                throw e; // Repassa MatriculaDuplicadaException ou SalaSemCapacidadeException intacta
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar matrícula: " + e.getMessage(), e);
        }
    }

    // Cancelar uma matrícula específica devolvendo a vaga na mesma transação
    public void cancelarMatricula(int idAluno, int codigoDisciplina, String semestre) {
        String sqlDeletar = "DELETE FROM matricula WHERE id_aluno = ? AND codigo_disciplina = ? AND semestre = ?";
        String sqlDevolverVaga = "UPDATE disciplina SET vagas_disponiveis = MIN(vagas, vagas_disponiveis + 1) WHERE codigo = ?";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false);
            try {
                int linhasApagadas;
                try (PreparedStatement psDel = conn.prepareStatement(sqlDeletar)) {
                    psDel.setInt(1, idAluno);
                    psDel.setInt(2, codigoDisciplina);
                    psDel.setString(3, semestre);
                    linhasApagadas = psDel.executeUpdate();
                }

                if (linhasApagadas > 0) {
                    try (PreparedStatement psVaga = conn.prepareStatement(sqlDevolverVaga)) {
                        psVaga.setInt(1, codigoDisciplina);
                        psVaga.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar matrícula: " + e.getMessage(), e);
        }
    }

    // RF3.8 (extra): Salva as 4 notas, média e situação calculada na classe Matricula
    public void atualizarNotasESituacao(Matricula matricula) {
        matricula.calcularMedia();

        String sql = """
            UPDATE matricula
            SET nota01 = ?, nota02 = ?, nota03 = ?, nota04 = ?, media = ?, situacao = ?
            WHERE id_aluno = ? AND codigo_disciplina = ? AND semestre = ?
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, matricula.getNota01());
            ps.setDouble(2, matricula.getNota02());
            ps.setDouble(3, matricula.getNota03());
            ps.setDouble(4, matricula.getNota04());
            ps.setDouble(5, matricula.getMedia());
            ps.setString(6, matricula.getSituacao());
            ps.setInt(7, matricula.getAluno().getId());
            ps.setInt(8, matricula.getDisciplina().getCodigo());
            ps.setString(9, matricula.getSemestre());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar notas e situação: " + e.getMessage(), e);
        }
    }

    public Matricula buscarMatricula(int idAluno, int codigoDisciplina, String semestre) {
        String sql = "SELECT * FROM matricula WHERE id_aluno = ? AND codigo_disciplina = ? AND semestre = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            ps.setInt(2, codigoDisciplina);
            ps.setString(3, semestre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarMatricula(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar matrícula: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Matricula> listarPorAluno(int idAluno) {
        List<Matricula> lista = new ArrayList<>();
        String sql = "SELECT * FROM matricula WHERE id_aluno = ? ORDER BY semestre";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarMatricula(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar matrículas do aluno: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Matricula> listarPorDisciplina(int codigoDisciplina) {
        List<Matricula> lista = new ArrayList<>();
        String sql = "SELECT * FROM matricula WHERE codigo_disciplina = ? ORDER BY semestre";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, codigoDisciplina);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarMatricula(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar matrículas da disciplina: " + e.getMessage(), e);
        }
        return lista;
    }

    private Matricula montarMatricula(ResultSet rs) throws SQLException {
        Aluno aluno = alunoDAO.buscarPorId(rs.getInt("id_aluno"));
        Disciplina disciplina = disciplinaDAO.buscarPorCodigo(rs.getInt("codigo_disciplina"));

        Matricula m = new Matricula(aluno, disciplina, rs.getString("semestre"));
        m.setId(rs.getInt("id"));
        m.carregarNotasDoBanco(
            rs.getDouble("nota01"),
            rs.getDouble("nota02"),
            rs.getDouble("nota03"),
            rs.getDouble("nota04"),
            rs.getDouble("media"),
            rs.getString("situacao")
        );
        return m;
    }
}
