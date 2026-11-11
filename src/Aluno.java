package src;

public class Aluno extends Pessoa {
    private int matricula;
    private String email;
    private Curso curso;

    public Aluno(int id, String nome, int matricula, String email) {
        super(id, nome);
        this.matricula = matricula;
        this.email = email;
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
        this.matricula = matricula;
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