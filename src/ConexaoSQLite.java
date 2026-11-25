import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoSQLite {
    // O arquivo sar.db será criado automaticamente na pasta do projeto
    private static final String URL = "jdbc:sqlite:sar.db";

    public static Connection conectar() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // No SQLite, é obrigatório ligar as chaves estrangeiras em cada conexão
        // para o ON DELETE CASCADE e ON DELETE SET NULL funcionarem
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void inicializarBanco() {
        String sqlCurso = """
            CREATE TABLE IF NOT EXISTS curso (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                carga_horaria INTEGER NOT NULL
            );
        """;

        String sqlSala = """
            CREATE TABLE IF NOT EXISTS sala (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                bloco TEXT NOT NULL,
                numero INTEGER NOT NULL,
                capacidade INTEGER NOT NULL,
                UNIQUE(bloco, numero)
            );
        """;

        // Endereço opcional no professor (herdado de Pessoa)
        String sqlProfessor = """
            CREATE TABLE IF NOT EXISTS professor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                departamento TEXT NOT NULL,
                rua TEXT,
                numero INTEGER,
                cidade TEXT,
                uf TEXT
            );
        """;

        // Endereço obrigatório no aluno (composição) + matrícula única (RN3.6)
        // Se o curso for apagado, o aluno continua no banco (ON DELETE SET NULL)
        String sqlAluno = """
            CREATE TABLE IF NOT EXISTS aluno (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                matricula INTEGER NOT NULL UNIQUE,
                email TEXT NOT NULL,
                rua TEXT NOT NULL,
                numero INTEGER NOT NULL,
                cidade TEXT NOT NULL,
                uf TEXT NOT NULL,
                id_curso INTEGER,
                FOREIGN KEY (id_curso) REFERENCES curso(id) ON DELETE SET NULL
            );
        """;

        // Se o curso for apagado, a disciplina fica e só perde o vínculo (RN3.5)
        String sqlDisciplina = """
            CREATE TABLE IF NOT EXISTS disciplina (
                codigo INTEGER PRIMARY KEY,
                nome TEXT NOT NULL,
                carga_horaria INTEGER NOT NULL,
                vagas INTEGER NOT NULL,
                vagas_disponiveis INTEGER NOT NULL,
                id_curso INTEGER,
                id_professor INTEGER,
                id_sala INTEGER,
                FOREIGN KEY (id_curso) REFERENCES curso(id) ON DELETE SET NULL,
                FOREIGN KEY (id_professor) REFERENCES professor(id) ON DELETE SET NULL,
                FOREIGN KEY (id_sala) REFERENCES sala(id) ON DELETE SET NULL
            );
        """;

        // UNIQUE impede matrícula repetida no mesmo semestre (RN3.1)
        // ON DELETE CASCADE apaga as matrículas quando o aluno é removido (RN3.4)
        String sqlMatricula = """
            CREATE TABLE IF NOT EXISTS matricula (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_aluno INTEGER NOT NULL,
                codigo_disciplina INTEGER NOT NULL,
                semestre TEXT NOT NULL,
                nota01 REAL DEFAULT 0,
                nota02 REAL DEFAULT 0,
                nota03 REAL DEFAULT 0,
                nota04 REAL DEFAULT 0,
                media REAL DEFAULT 0,
                situacao TEXT DEFAULT 'Cursando',
                UNIQUE(id_aluno, codigo_disciplina, semestre),
                FOREIGN KEY (id_aluno) REFERENCES aluno(id) ON DELETE CASCADE,
                FOREIGN KEY (codigo_disciplina) REFERENCES disciplina(codigo) ON DELETE CASCADE
            );
        """;

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCurso);
            stmt.execute(sqlSala);
            stmt.execute(sqlProfessor);
            stmt.execute(sqlAluno);
            stmt.execute(sqlDisciplina);
            stmt.execute(sqlMatricula);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar o banco de dados: " + e.getMessage(), e);
        }
    }
}
