import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File dbAntigo = new File("sar.db");
        if (dbAntigo.exists()) dbAntigo.delete();

        // 1. Cria o arquivo sar.db com as 6 tabelas e chaves estrangeiras
        ConexaoSQLite.inicializarBanco();
        System.out.println("=== Banco sar.db inicializado com sucesso! ===\n");

        CursoDAO cursoDAO = new CursoDAO();
        SalaDAO salaDAO = new SalaDAO();
        ProfessorDAO professorDAO = new ProfessorDAO();
        AlunoDAO alunoDAO = new AlunoDAO();
        DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
        MatriculaDAO matriculaDAO = new MatriculaDAO();

        // 2. RF3.1: Cadastrar Curso, Sala, Professor (com endereço), Aluno (com endereço) e Disciplina
        Curso curso = new Curso(0, "Engenharia da Computação", 3600);
        cursoDAO.salvar(curso);

        Sala sala = new Sala('B', 1, 50);
        salaDAO.salvar(sala);

        Professor professor = new Professor(
            "Dr. Michael Cruz",
            "TI",
            new Endereco("Av. Universitária", 500, "Belo Jardim", "PE")
        );
        professorDAO.salvar(professor);

        Aluno aluno1 = new Aluno(
            "Emily Lauana",
            11112,
            "emily.lauana@email.com",
            new Endereco("Rua das Flores", 123, "Recife", "PE")
        );
        aluno1.setCurso(curso);
        alunoDAO.salvar(aluno1);

        Disciplina disciplina = new Disciplina(101, "Programação Java", 90, 40);
        disciplinaDAO.salvar(disciplina);

        // RF3.2 e RF3.3: Associar disciplina ao curso e atribuir professor e sala
        cursoDAO.associarDisciplina(curso.getIdCurso(), disciplina.getCodigo());
        disciplinaDAO.atribuirProfessorESala(disciplina.getCodigo(), professor.getId(), sala.getId());

        // RF3.1: Testando Edição (Update) e Listagem
        aluno1.setEmail("emily.atualizado@email.com");
        alunoDAO.atualizar(aluno1);

        System.out.println("--- Alunos cadastrados no banco (RF3.1) ---");
        for (Aluno a : alunoDAO.listarTodos()) {
            System.out.println(a.getId() + " | " + a.getNome() + " | Mat: " + a.getMatricula()
                + " | Endereço: " + a.getEndereco()
                + " | Curso: " + (a.getCurso() != null ? a.getCurso().getNome() : "Sem curso"));
        }

        // 3. RF3.4: Matrícula + desconto de vaga juntos na mesma transação
        System.out.println("\n--- Teste de Matrícula e Vagas (RF3.4) ---");
        System.out.println("Vagas disponíveis antes da matrícula: " + disciplinaDAO.buscarPorCodigo(101).getVagasDisponiveis());

        Matricula m1 = new Matricula(aluno1, disciplina, "2026.1");
        matriculaDAO.matricular(m1);
        System.out.println("Matrícula em 2026.1 realizada! Vagas disponíveis agora: "
            + disciplinaDAO.buscarPorCodigo(101).getVagasDisponiveis());

        // RN3.1: Tentativa de matrícula duplicada no mesmo semestre
        try {
            matriculaDAO.matricular(new Matricula(aluno1, disciplina, "2026.1"));
        } catch (MatriculaDuplicadaException e) {
            System.out.println("Bloqueio RN3.1 funcionou: " + e.getMessage());
        }

        // Mesmo aluno na mesma disciplina em OUTRO semestre (permitido)
        Matricula m2 = new Matricula(aluno1, disciplina, "2026.2");
        matriculaDAO.matricular(m2);
        System.out.println("Matrícula em 2026.2 realizada! Vagas disponíveis agora: "
            + disciplinaDAO.buscarPorCodigo(101).getVagasDisponiveis());

        // RN3.6: Tentativa de cadastrar aluno com matrícula repetida
        try {
            Aluno duplicado = new Aluno("Outro Aluno", 11112, "outro@email.com",
                new Endereco("Rua B", 10, "Recife", "PE"));
            alunoDAO.salvar(duplicado);
        } catch (RegistroDuplicadoException e) {
            System.out.println("Bloqueio RN3.6 funcionou: " + e.getMessage());
        }

        // RN3.2: Disciplina com vagas maiores que a capacidade da sala
        Disciplina discLotada = new Disciplina(102, "Banco de Dados", 60, 60);
        disciplinaDAO.salvar(discLotada);
        try {
            disciplinaDAO.atribuirProfessorESala(102, professor.getId(), sala.getId());
        } catch (SalaSemCapacidadeException e) {
            System.out.println("Bloqueio RN3.2 funcionou: " + e.getMessage());
        }

        // Consultas que alimentam RF3.5, RF3.6 e as futuras telas de detalhes
        System.out.println("\n--- Consultas no Banco ---");
        System.out.println("Disciplinas de " + aluno1.getNome() + ": " + disciplinaDAO.buscarPorAluno(aluno1.getId()).size());
        System.out.println("Alunos em " + disciplina.getNome() + ": " + alunoDAO.buscarPorDisciplina(101).size());
        System.out.println("Disciplinas do prof. " + professor.getNome() + ": " + professorDAO.contarDisciplinasMinistradas(professor.getId()));

        // RN3.5: Remover o curso mantém a disciplina no banco (só zera o vínculo)
        cursoDAO.remover(curso.getIdCurso());
        Disciplina discAposRemoverCurso = disciplinaDAO.buscarPorCodigo(101);
        System.out.println("\nRN3.5 - Após remover curso, disciplina 101 continua no banco? "
            + (discAposRemoverCurso != null) + " | Curso vinculado: " + discAposRemoverCurso.getCurso());

        // RN3.4: Remover aluno apaga o endereço, apaga as matrículas (CASCADE) e devolve as vagas
        alunoDAO.remover(aluno1.getId());
        List<Matricula> matriculasRestantes = matriculaDAO.listarPorAluno(aluno1.getId());
        System.out.println("RN3.4 - Após remover aluno, matrículas restantes: " + matriculasRestantes.size()
            + " | Vagas restauradas em 101: " + disciplinaDAO.buscarPorCodigo(101).getVagasDisponiveis());

        // 4. RF3.7: Importar alunos de um arquivo CSV salvando no SQLite
        System.out.println("\n--- Importação de Alunos via CSV (RF3.7) ---");
        try {
            ImportadorCSV.ResultadoImportacao resultado = ImportadorCSV.importarAlunos("alunos.csv", alunoDAO);
            System.out.println("Importação CSV concluída: " + resultado.getResumo());
            for (Aluno a : alunoDAO.listarTodos()) {
                System.out.println("- No banco: ID " + a.getId() + " | " + a.getNome()
                    + " | Mat: " + a.getMatricula() + " | Endereço: " + a.getEndereco());
            }
            for (String erro : resultado.getErros()) {
                System.out.println("- " + erro);
            }
        } catch (ArquivoInvalidoException e) {
            System.out.println("Erro no CSV: " + e.getMessage());
        }
    }
}
