package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelo.Disciplina;
import modelo.RegistroDuplicadoException;
import modelo.Sala;
import modelo.SalaSemCapacidadeException;

public class DisciplinaDAO {
    private final CursoDAO cursoDAO = new CursoDAO();
    private final ProfessorDAO professorDAO = new ProfessorDAO();
    private final SalaDAO salaDAO = new SalaDAO();

    public void salvar(Disciplina disciplina) {
        if (disciplina.getSala() != null && disciplina.getVagas() > disciplina.getSala().getCapacidade()) {
            throw new SalaSemCapacidadeException("Número de vagas (" + disciplina.getVagas()
                + ") excede a capacidade da sala (" + disciplina.getSala().getCapacidade() + ").");
        }

        String sql = "INSERT INTO disciplina (codigo, nome, carga_horaria, vagas, vagas_disponiveis, id_curso, id_professor, id_sala) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, disciplina.getCodigo());
            ps.setString(2, disciplina.getNome());
            ps.setInt(3, disciplina.getCargaHoraria());
            ps.setInt(4, disciplina.getVagas());
            ps.setInt(5, disciplina.getVagasDisponiveis());

            if (disciplina.getCurso() != null && disciplina.getCurso().getIdCurso() > 0) {
                ps.setInt(6, disciplina.getCurso().getIdCurso());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            if (disciplina.getProfessor() != null && disciplina.getProfessor().getId() > 0) {
                ps.setInt(7, disciplina.getProfessor().getId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            if (disciplina.getSala() != null && disciplina.getSala().getId() > 0) {
                ps.setInt(8, disciplina.getSala().getId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY KEY"))) {
                throw new RegistroDuplicadoException("Código de disciplina já cadastrado: " + disciplina.getCodigo());
            }
            throw new RuntimeException("Erro ao salvar disciplina: " + e.getMessage(), e);
        }
    }

    public void atualizar(Disciplina disciplina) {
        if (disciplina.getSala() != null && disciplina.getVagas() > disciplina.getSala().getCapacidade()) {
            throw new SalaSemCapacidadeException("Número de vagas excede a capacidade da sala.");
        }

        String sql = "UPDATE disciplina SET nome = ?, carga_horaria = ?, vagas = ?, vagas_disponiveis = ?, id_curso = ?, id_professor = ?, id_sala = ? WHERE codigo = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, disciplina.getNome());
            ps.setInt(2, disciplina.getCargaHoraria());
            ps.setInt(3, disciplina.getVagas());
            ps.setInt(4, disciplina.getVagasDisponiveis());

            if (disciplina.getCurso() != null && disciplina.getCurso().getIdCurso() > 0) {
                ps.setInt(5, disciplina.getCurso().getIdCurso());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (disciplina.getProfessor() != null && disciplina.getProfessor().getId() > 0) {
                ps.setInt(6, disciplina.getProfessor().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            if (disciplina.getSala() != null && disciplina.getSala().getId() > 0) {
                ps.setInt(7, disciplina.getSala().getId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setInt(8, disciplina.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar disciplina: " + e.getMessage(), e);
        }
    }

    public void remover(int codigo) {
        String sql = "DELETE FROM disciplina WHERE codigo = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover disciplina: " + e.getMessage(), e);
        }
    }

    public Disciplina buscarPorCodigo(int codigo) {
        String sql = "SELECT * FROM disciplina WHERE codigo = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarDisciplina(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar disciplina: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Disciplina> listarTodas() {
        List<Disciplina> disciplinas = new ArrayList<>();
        String sql = "SELECT * FROM disciplina ORDER BY nome";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                disciplinas.add(montarDisciplina(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas: " + e.getMessage(), e);
        }
        return disciplinas;
    }

    // RF3.3 + RN3.2: Valida se as vagas cabem na sala antes de atribuir
    public void atribuirProfessorESala(int codigoDisciplina, int idProfessor, int idSala) {
        Disciplina disciplina = buscarPorCodigo(codigoDisciplina);
        Sala sala = salaDAO.buscarPorId(idSala);

        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina não encontrada: " + codigoDisciplina);
        }
        if (sala == null) {
            throw new IllegalArgumentException("Sala não encontrada: " + idSala);
        }
        if (disciplina.getVagas() > sala.getCapacidade()) {
            throw new SalaSemCapacidadeException("Número de vagas (" + disciplina.getVagas()
                + ") excede a capacidade da sala (" + sala.getCapacidade() + ").");
        }

        String sql = "UPDATE disciplina SET id_professor = ?, id_sala = ? WHERE codigo = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfessor);
            ps.setInt(2, idSala);
            ps.setInt(3, codigoDisciplina);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atribuir professor e sala: " + e.getMessage(), e);
        }
    }

    // RF3.5: Consultar as disciplinas de um aluno
    public List<Disciplina> buscarPorAluno(int idAluno) {
        List<Disciplina> disciplinas = new ArrayList<>();
        String sql = """
            SELECT DISTINCT d.* FROM disciplina d
            INNER JOIN matricula m ON m.codigo_disciplina = d.codigo
            WHERE m.id_aluno = ?
            ORDER BY d.nome
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAluno);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    disciplinas.add(montarDisciplina(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar disciplinas do aluno: " + e.getMessage(), e);
        }
        return disciplinas;
    }

    // RF3.6: Listar disciplinas ministradas por um professor
    public List<Disciplina> buscarPorProfessor(int idProfessor) {
        List<Disciplina> disciplinas = new ArrayList<>();
        String sql = "SELECT * FROM disciplina WHERE id_professor = ? ORDER BY nome";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfessor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    disciplinas.add(montarDisciplina(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar disciplinas do professor: " + e.getMessage(), e);
        }
        return disciplinas;
    }

    private Disciplina montarDisciplina(ResultSet rs) throws SQLException {
        Disciplina disciplina = new Disciplina(
            rs.getInt("codigo"),
            rs.getString("nome"),
            rs.getInt("carga_horaria"),
            rs.getInt("vagas")
        );
        disciplina.setVagasDisponiveis(rs.getInt("vagas_disponiveis"));

        if (rs.getObject("id_curso") != null) {
            disciplina.setCurso(cursoDAO.buscarPorId(rs.getInt("id_curso")));
        }
        if (rs.getObject("id_professor") != null) {
            disciplina.setProfessor(professorDAO.buscarPorId(rs.getInt("id_professor")));
        }
        if (rs.getObject("id_sala") != null) {
            disciplina.setSala(salaDAO.buscarPorId(rs.getInt("id_sala")));
        }

        return disciplina;
    }
}
