package src;
public class Disciplina {
    private Professor professor;
    private Sala sala;

    private int codigo;
    private String nome;
    private int cargaHoraria;
    private int vagas;

    public Disciplina(int codigo, String nome, int cargaHoraria, int vagas) {
        this.codigo = codigo;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.vagas = vagas;
    }

    public void atribuirProfessor(Professor professor) {
        this.professor = professor;
    }

    public void atribuirSala(Sala sala) {
        this.sala = sala;
    }

    public Professor getProfessor() {
        return professor;
    }

    public Sala getSala() {
        return sala;
    }
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public int getVagas() {
        return vagas;
    }

    public void setVagas(int vagas) {
        this.vagas = vagas;
    }
}