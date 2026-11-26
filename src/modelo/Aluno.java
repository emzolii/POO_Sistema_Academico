package modelo;
public class Aluno extends Pessoa {
    private static int contador = 1;
    private int matricula;
    private String email;
    private Curso curso;

    public Aluno(int id, String nome, int matricula, String email) {
        super(id, nome);
        this.matricula = matricula;
        this.email = email;
    }

    public Aluno(int id, String nome, int matricula, String email, Endereco endereco) {
        super(id, nome, endereco);
        this.matricula = matricula;
        this.email = email;
    }

    public Aluno(int id, String nome, int matricula, String email, String rua, int numero, String cidade, String uf) {
        super(id, nome, new Endereco(rua, numero, cidade, uf));
        this.matricula = matricula;
        this.email = email;
    }

    public Aluno(String nome, int matricula, String email, Endereco endereco) {
        this(0, nome, matricula, email, endereco);
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
