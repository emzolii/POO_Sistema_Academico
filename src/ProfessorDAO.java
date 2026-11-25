import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {

    public void salvar(Professor professor) {
        String sql = "INSERT INTO professor (nome, departamento, rua, numero, cidade, uf) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, professor.getNome());
            ps.setString(2, professor.getDepartamento());
            preencherEnderecoNoStatement(ps, professor.getEndereco(), 3);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    professor.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar professor: " + e.getMessage(), e);
        }
    }

    public void atualizar(Professor professor) {
        String sql = "UPDATE professor SET nome = ?, departamento = ?, rua = ?, numero = ?, cidade = ?, uf = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, professor.getNome());
            ps.setString(2, professor.getDepartamento());
            preencherEnderecoNoStatement(ps, professor.getEndereco(), 3);
            ps.setInt(7, professor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar professor: " + e.getMessage(), e);
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM professor WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover professor: " + e.getMessage(), e);
        }
    }

    public Professor buscarPorId(int id) {
        String sql = "SELECT * FROM professor WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarProfessor(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar professor: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Professor> listarTodos() {
        List<Professor> professores = new ArrayList<>();
        String sql = "SELECT * FROM professor ORDER BY nome";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                professores.add(montarProfessor(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar professores: " + e.getMessage(), e);
        }
        return professores;
    }

    // RF3.6: Consultar quantas disciplinas um professor ministra
    public int contarDisciplinasMinistradas(int idProfessor) {
        String sql = "SELECT COUNT(*) FROM disciplina WHERE id_professor = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProfessor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar disciplinas do professor: " + e.getMessage(), e);
        }
        return 0;
    }

    private void preencherEnderecoNoStatement(PreparedStatement ps, Endereco end, int indiceInicial) throws SQLException {
        if (end != null) {
            ps.setString(indiceInicial, end.getRua());
            ps.setInt(indiceInicial + 1, end.getNumero());
            ps.setString(indiceInicial + 2, end.getCidade());
            ps.setString(indiceInicial + 3, end.getUf());
        } else {
            ps.setNull(indiceInicial, Types.VARCHAR);
            ps.setNull(indiceInicial + 1, Types.INTEGER);
            ps.setNull(indiceInicial + 2, Types.VARCHAR);
            ps.setNull(indiceInicial + 3, Types.VARCHAR);
        }
    }

    private Professor montarProfessor(ResultSet rs) throws SQLException {
        Endereco endereco = null;
        if (rs.getString("rua") != null) {
            endereco = new Endereco(
                rs.getString("rua"),
                rs.getInt("numero"),
                rs.getString("cidade"),
                rs.getString("uf")
            );
        }
        return new Professor(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("departamento"),
            endereco
        );
    }
}
