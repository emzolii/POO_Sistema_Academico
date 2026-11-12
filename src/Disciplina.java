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

    /***
     * Atribui um professor à disciplina.
     * @param professor O professor a ser atribuído.
     * @return true se o professor foi atribuído com sucesso, false caso contrário.
     */
    public boolean atribuirProfessor(Professor professor) {

    if (this.professor != null) {
        return false;
    }

    this.professor = professor;
    return true;
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