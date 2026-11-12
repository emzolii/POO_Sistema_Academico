package src;
import java.util.ArrayList;
import java.util.List;

public class Aluno extends Pessoa {
    private static int contador = 1;
    private int matricula;
    private String email;
    private Curso curso;
    private static List<Integer> matriculasUtilizadas = new ArrayList<>();

    public Aluno(int id, String nome, int matricula, String email) {
        super(id, nome);
        if (matriculasUtilizadas.contains(matricula)) {
            throw new RuntimeException("Matrícula já utilizada.");
        }
        matriculasUtilizadas.add(matricula);
        this.matricula = matricula;
        this.email = email;
    }

    public static int gerarMatricula(String periodo) {
    String numeroMatricula = periodo + String.format("%03d", contador);
    contador++;

    return Integer.parseInt(numeroMatricula);
} 

    public Curso getCurso() { 
        return curso; 
    }
    public void setCurso(Curso curso) { 
        this.curso = curso; 
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        if (matriculasUtilizadas.contains(matricula)) {
            throw new RuntimeException("Matrícula já utilizada.");
        }
        matriculasUtilizadas.remove(Integer.valueOf(this.matricula));
        this.matricula = matricula;
        matriculasUtilizadas.add(matricula);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPapel() {
        return "Aluno";
    }
}