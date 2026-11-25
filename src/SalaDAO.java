import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    public void salvar(Sala sala) {
        String sql = "INSERT INTO sala (bloco, numero, capacidade) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, String.valueOf(sala.getBloco()));
            ps.setInt(2, sala.getNumero());
            ps.setInt(3, sala.getCapacidade());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    sala.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                throw new RegistroDuplicadoException("Já existe uma sala cadastrada no bloco " + sala.getBloco() + " número " + sala.getNumero());
            }
            throw new RuntimeException("Erro ao salvar sala: " + e.getMessage(), e);
        }
    }

    public void atualizar(Sala sala) {
        String sql = "UPDATE sala SET bloco = ?, numero = ?, capacidade = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, String.valueOf(sala.getBloco()));
            ps.setInt(2, sala.getNumero());
            ps.setInt(3, sala.getCapacidade());
            ps.setInt(4, sala.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                throw new RegistroDuplicadoException("Já existe uma sala cadastrada no bloco " + sala.getBloco() + " número " + sala.getNumero());
            }
            throw new RuntimeException("Erro ao atualizar sala: " + e.getMessage(), e);
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM sala WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover sala: " + e.getMessage(), e);
        }
    }

    public Sala buscarPorId(int id) {
        String sql = "SELECT * FROM sala WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarSala(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sala: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Sala> listarTodas() {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT * FROM sala ORDER BY bloco, numero";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                salas.add(montarSala(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar salas: " + e.getMessage(), e);
        }
        return salas;
    }

    private Sala montarSala(ResultSet rs) throws SQLException {
        return new Sala(
            rs.getInt("id"),
            rs.getString("bloco").charAt(0),
            rs.getInt("numero"),
            rs.getInt("capacidade")
        );
    }
}
