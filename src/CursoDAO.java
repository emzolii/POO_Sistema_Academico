import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public void salvar(Curso curso) {
        String sql = "INSERT INTO curso (nome, carga_horaria) VALUES (?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, curso.getNome());
            ps.setInt(2, curso.getCargaHoraria());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    curso.setIdCurso(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar curso: " + e.getMessage(), e);
        }
    }

    public void atualizar(Curso curso) {
        String sql = "UPDATE curso SET nome = ?, carga_horaria = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, curso.getNome());
            ps.setInt(2, curso.getCargaHoraria());
            ps.setInt(3, curso.getIdCurso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar curso: " + e.getMessage(), e);
        }
    }

    // RN3.5: O banco tem ON DELETE SET NULL em disciplina e aluno,
    // então remover o curso só remove o vínculo e mantém as disciplinas intactas.
    public void remover(int id) {
        String sql = "DELETE FROM curso WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover curso: " + e.getMessage(), e);
        }
    }

    public Curso buscarPorId(int id) {
        String sql = "SELECT * FROM curso WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Curso(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("carga_horaria")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar curso: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Curso> listarTodos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM curso ORDER BY nome";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cursos.add(new Curso(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getInt("carga_horaria")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cursos: " + e.getMessage(), e);
        }
        return cursos;
    }

    public void associarDisciplina(int idCurso, int codigoDisciplina) {
        String sql = "UPDATE disciplina SET id_curso = ? WHERE codigo = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            ps.setInt(2, codigoDisciplina);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao associar disciplina ao curso: " + e.getMessage(), e);
        }
    }

    public void associarAluno(int idCurso, int idAluno) {
        String sql = "UPDATE aluno SET id_curso = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            ps.setInt(2, idAluno);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao associar aluno ao curso: " + e.getMessage(), e);
        }
    }
}
