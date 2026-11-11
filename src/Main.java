package src;
import java.util.List;

public class Main {
    public static void main(String[] args){

        Aluno aluno1 = new Aluno(1, "Emily Lauana", 11112, "emily.lauana@email.com");
        Disciplina disciplina1 = new Disciplina(101, "Programação Java",90,40);
        Professor professor1 = new Professor(1, "Dr. Michael Cruz", "TI");
        Sala sala1 = new Sala('B', 1, 50);

        //Ligar a disciplina e o aluno a um curso RF3.2
        Curso curso1 = new Curso(1, "Engenharia da Computação", 3600);
        curso1.addAluno(aluno1);
        curso1.addDisciplina(disciplina1);

        for (Aluno aluno : curso1.getAlunos()) {
            System.out.println("Aluno matriculado no curso: " + aluno.getNome());
        }

        for (Disciplina disciplina : curso1.getDisciplinas()) {
            System.out.println("Disciplina do curso: " + disciplina.getNome());
        }

        //Escolher um professor e uma sala para a disciplina RF3.3
        disciplina1.atribuirProfessor(professor1);
        disciplina1.atribuirSala(sala1);

        System.out.println("Professor da disciplina " + disciplina1.getNome() + ": " + disciplina1.getProfessor().getNome());
        System.out.println("Sala da disciplina " + disciplina1.getNome() + ": " + disciplina1.getSala().getBloco() + " - " + disciplina1.getSala().getNumero());

        //Teste da matrícula duplicada RF3.4
        //Primeira tentativa: Matricular Emily em Java
        Matricula m1 = new Matricula(aluno1, disciplina1, "2026.1");
        boolean resultadoMatricula = m1.matricular();
        System.out.println("1. Matrícula de Emily em Java: " + (resultadoMatricula));

        //Segunda tentativa: Matricular Emily em Java
        Matricula m2 = new Matricula(aluno1, disciplina1, "2026.1");
        boolean resultadoMatricula02 = m2.matricular();
        System.out.println("2. Tentativa de duplicar matrícula de Emily: " + (resultadoMatricula02));

        List<Matricula> lista = Matricula.getListaGeral();

        for (int i = 0; i < lista.size(); i++) {
            Matricula m = lista.get(i);
            System.out.println((i + 1) + "ª Matrícula - Aluno: " + m.getAluno().getNome());
        }

        
    }
}
