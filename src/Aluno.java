import java.util.ArrayList;
import java.util.List;

public class Aluno extends Pessoa {
    private static int contador = 1;
    private int matricula;
    private String email;
    private Curso curso;
    private Endereco endereco;
    private static List<Integer> matriculasUtilizadas = new ArrayList<>();

    public Aluno(int id, String nome, int matricula, String email) {
        super(id, nome);
        if (matriculasUtilizadas.contains(matricula)) {
            throw new RegistroDuplicadoException("Matrícula já utilizada: " + matricula);
        }
        matriculasUtilizadas.add(matricula);
        this.matricula = matricula;
        this.email = email;
    }

    public Aluno(int id, String nome, int matricula, String email, Endereco endereco) {
        this(id, nome, matricula, email);
        this.endereco = endereco;
    }

    public Aluno(int id, String nome, int matricula, String email, String rua, int numero, String cidade, String uf) {
        this(id, nome, matricula, email);
        this.endereco = new Endereco(rua, numero, cidade, uf);
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

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void setEndereco(String rua, int numero, String cidade, String uf) {
        this.endereco = new Endereco(rua, numero, cidade, uf);
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        if (matriculasUtilizadas.contains(matricula)) {
            throw new RegistroDuplicadoException("Matrícula já utilizada: " + matricula);
        }
        matriculasUtilizadas.remove(Integer.valueOf(this.matricula));
        this.matricula = matricula;
        matriculasUtilizadas.add(matricula);
    }

    public List<Disciplina> consultarDisciplinas() {
        List<Disciplina> minhasDisciplinas = new ArrayList<>();
        
        for (Matricula m : Matricula.getListaGeral()) {
            if (m.getAluno().getId() == this.getId()) {
                if (!minhasDisciplinas.contains(m.getDisciplina())) {
                    minhasDisciplinas.add(m.getDisciplina());
                }
            }
        }
        return minhasDisciplinas;
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
