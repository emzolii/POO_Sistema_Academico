package src;
import java.util.ArrayList;
import java.util.List;

public class Curso {
    private List<Disciplina> disciplinas;
    private List<Aluno> alunos;

    private int idCurso;
    private String nome;
    private int cargaHoraria;

    public Curso(int idCurso, String nome, int cargaHoraria) {
        this.idCurso = idCurso;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.disciplinas = new ArrayList<>();
        this.alunos = new ArrayList<>();
    }

    public boolean addAluno(Aluno aluno) {
        if (aluno != null && !this.alunos.contains(aluno)) {
            this.alunos.add(aluno);
            return true;
        }
        return false;
    }

    public boolean removerAluno(Aluno aluno) {
        return this.alunos.remove(aluno);
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void addDisciplina(Disciplina disciplina) {
        if (disciplina != null && !this.disciplinas.contains(disciplina)) {
            this.disciplinas.add(disciplina);
        }
    }

    public void removerDisciplina(Disciplina disciplina) {
        this.disciplinas.remove(disciplina);
    }

    public List<Disciplina> getDisciplinas() {
    return disciplinas;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
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
}