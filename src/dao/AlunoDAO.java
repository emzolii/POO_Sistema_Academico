package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelo.Aluno;
import modelo.Endereco;
import modelo.RegistroDuplicadoException;

public class AlunoDAO {
    private final CursoDAO cursoDAO = new CursoDAO();

    public void salvar(Aluno aluno) {
        if (aluno.getEndereco() == null) {
            throw new IllegalArgumentException("O endereço do aluno é obrigatório.");
        }

        String sql = "INSERT INTO aluno (nome, matricula, email, rua, numero, cidade, uf, id_curso) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, aluno.getNome());
            ps.setInt(2, aluno.getMatricula());
            ps.setString(3, aluno.getEmail());
            ps.setString(4, aluno.getEndereco().getRua());
            ps.setInt(5, aluno.getEndereco().getNumero());
            ps.setString(6, aluno.getEndereco().getCidade());
            ps.setString(7, aluno.getEndereco().getUf());

            if (aluno.getCurso() != null && aluno.getCurso().getIdCurso() > 0) {
                ps.setInt(8, aluno.getCurso().getIdCurso());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    aluno.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            // RN3.6: Matrícula do aluno é única no sistema
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                throw new RegistroDuplicadoException("Matrícula já utilizada: " + aluno.getMatricula());
            }
            throw new RuntimeException("Erro ao salvar aluno: " + e.getMessage(), e);
        }
    }

    public void atualizar(Aluno aluno) {
        if (aluno.getEndereco() == null) {
            throw new IllegalArgumentException("O endereço do aluno é obrigatório.");
        }

        String sql = "UPDATE aluno SET nome = ?, matricula = ?, email = ?, rua = ?, numero = ?, cidade = ?, uf = ?, id_curso = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, aluno.getNome());
            ps.setInt(2, aluno.getMatricula());
            ps.setString(3, aluno.getEmail());
            ps.setString(4, aluno.getEndereco().getRua());
            ps.setInt(5, aluno.getEndereco().getNumero());
            ps.setString(6, aluno.getEndereco().getCidade());
            ps.setString(7, aluno.getEndereco().getUf());

            if (aluno.getCurso() != null && aluno.getCurso().getIdCurso() > 0) {
                ps.setInt(8, aluno.getCurso().getIdCurso());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setInt(9, aluno.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                throw new RegistroDuplicadoException("Matrícula já utilizada: " + aluno.getMatricula());
            }
            throw new RuntimeException("Erro ao atualizar aluno: " + e.getMessage(), e);
        }
    }

    // RN3.4: Ao remover um aluno, o endereço (na mesma linha) e as matrículas (ON DELETE CASCADE)
    // também são removidos, e as vagas das disciplinas voltam a ficar disponíveis.
    public void remover(int id) {
        String sqlBuscarDisciplinas = "SELECT codigo_disciplina FROM matricula WHERE id_aluno = ?";
        String sqlDevolverVaga = "UPDATE disciplina SET vagas_disponiveis = MIN(vagas, vagas_disponiveis + 1) WHERE codigo = ?";
        String sqlDeletarAluno = "DELETE FROM aluno WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false);
            try {
                List<Integer> codigosDisciplinas = new ArrayList<>();
                try (PreparedStatement psBusca = conn.prepareStatement(sqlBuscarDisciplinas)) {
                    psBusca.setInt(1, id);
                    try (ResultSet rs = psBusca.executeQuery()) {
                        while (rs.next()) {
                            codigosDisciplinas.add(rs.getInt("codigo_disciplina"));
                        }
                    }
                }

                try (PreparedStatement psVaga = conn.prepareStatement(sqlDevolverVaga)) {
                    for (int codigo : codigosDisciplinas) {
                        psVaga.setInt(1, codigo);
                        psVaga.executeUpdate();
                    }
                }

                try (PreparedStatement psDel = conn.prepareStatement(sqlDeletarAluno)) {
                    psDel.setInt(1, id);
                    psDel.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover aluno: " + e.getMessage(), e);
        }
    }

    public Aluno buscarPorId(int id) {
        String sql = "SELECT * FROM aluno WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarAluno(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar aluno por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public Aluno buscarPorMatricula(int matricula) {
        String sql = "SELECT * FROM aluno WHERE matricula = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarAluno(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar aluno por matrícula: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Aluno> listarTodos() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT * FROM aluno ORDER BY nome";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alunos.add(montarAluno(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar alunos: " + e.getMessage(), e);
        }
        return alunos;
    }

    public List<Aluno> buscar(String nome) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT * FROM aluno WHERE LOWER(nome) LIKE LOWER(?) ORDER BY nome";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alunos.add(montarAluno(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar alunos por nome: " + e.getMessage(), e);
        }
        return alunos;
    }

    public List<Aluno> buscar(String nome, String semestre) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = """
            SELECT DISTINCT a.* FROM aluno a
            INNER JOIN matricula m ON m.id_aluno = a.id
            WHERE LOWER(a.nome) LIKE LOWER(?) AND m.semestre = ?
            ORDER BY a.nome
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nome + "%");
            ps.setString(2, semestre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alunos.add(montarAluno(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar alunos por nome e semestre: " + e.getMessage(), e);
        }
        return alunos;
    }

    // RF3.5: Consultar os alunos matriculados em uma disciplina
    public List<Aluno> buscarPorDisciplina(int codigoDisciplina) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = """
            SELECT DISTINCT a.* FROM aluno a
            INNER JOIN matricula m ON m.id_aluno = a.id
            WHERE m.codigo_disciplina = ?
            ORDER BY a.nome
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, codigoDisciplina);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alunos.add(montarAluno(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar alunos da disciplina: " + e.getMessage(), e);
        }
        return alunos;
    }

    private Aluno montarAluno(ResultSet rs) throws SQLException {
        Endereco endereco = new Endereco(
            rs.getString("rua"),
            rs.getInt("numero"),
            rs.getString("cidade"),
            rs.getString("uf")
        );

        Aluno aluno = new Aluno(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getInt("matricula"),
            rs.getString("email"),
            endereco
        );

        if (rs.getObject("id_curso") != null) {
            aluno.setCurso(cursoDAO.buscarPorId(rs.getInt("id_curso")));
        }

        return aluno;
    }
}
