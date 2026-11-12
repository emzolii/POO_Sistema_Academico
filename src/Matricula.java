package src;
import java.util.ArrayList;
import java.util.List;

public class Matricula {
    private Aluno aluno;
    private Disciplina disciplina;
    private double nota01;
    private double nota02;
    private double nota03;
    private double nota04;
    private double media;
    private String situacao;
    private String semestre;

    private static List<Matricula> listaGeral = new ArrayList<>();

    public Matricula(Aluno aluno, Disciplina disciplina, String semestre) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.semestre = semestre;
    }

    /*Esse método verifica se a lista recebida não é nula, depois busca 
    por um vínculo de aluno e disciplina que seja igual ao que está sendo recebido 
    para impedir matriculas duplicadas*/

    public boolean jaEstaNaLista(List<Matricula> lista) {
        if (lista == null || this.aluno == null || this.disciplina == null) {
            return false;
        }
        for (Matricula m : lista) {
            if (m.getAluno().getId() == this.aluno.getId() && 
                m.getDisciplina().getCodigo() == this.disciplina.getCodigo()) {
                return true;
            }
        }
        return false;
    }

    public boolean jaEstaNaLista() {
        return jaEstaNaLista(listaGeral);
    }

    /***
     * Matricula o aluno na disciplina.
     * @return true se a matrícula foi realizada com sucesso, false caso contrário.
     */
    public boolean matricular() {
        if (this.jaEstaNaLista()) {
            return false;
        }
        if (disciplina.getSala() == null) {
            throw new RuntimeException("Disciplina não possui sala.");
        }
        if (disciplina.getVagas() > disciplina.getSala().getCapacidade()) {
            throw new RuntimeException("Número de vagas excede a capacidade da sala.");
        }
        int quantidadeMatriculados = contarMatriculados(disciplina, semestre);
        if (quantidadeMatriculados >= disciplina.getVagas()) {
            throw new RuntimeException("Não há vagas disponíveis.");
        }
        listaGeral.add(this); 
        return true;
    }

    public static List<Matricula> getListaGeral() {
        return listaGeral;
    }

    public double calcularMedia() {
        double mediaInicial = (this.nota01 + this.nota02) / 2.0;

        if (mediaInicial >= 7.0) {
            this.media = mediaInicial;
            this.situacao = "Aprovado";
        } else {
            double maiorNota = Math.max(this.nota01, this.nota02);
            double mediaComNota03 = (maiorNota + this.nota03) / 2.0;

            if (mediaComNota03 >= 7.0) {
                this.media = mediaComNota03;
                this.situacao = "Aprovado";
            } else {
                this.media = (mediaComNota03 + this.nota04) / 2.0;
                this.situacao = (this.media >= 7.0) ? "Aprovado" : "Reprovado";
            }
        }
        return this.media;
    }
   
    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public double getNota01() {
        return nota01;
    }

    public void setNota01(double nota01) {
        this.nota01 = nota01;
        this.calcularMedia(); 
    }

    public double getNota02() {
        return nota02;
    }

    public void setNota02(double nota02) {
        this.nota02 = nota02;
        this.calcularMedia();
    }

    public double getNota03() {
        return nota03;
    }

    public void setNota03(double nota03) {
        this.nota03 = nota03;
        this.calcularMedia();
    }

    public double getNota04() {
        return nota04;
    }

    public void setNota04(double nota04) {
        this.nota04 = nota04;
        this.calcularMedia();
    }

    public double getMedia() {
        return media;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }
}
public static int contarMatriculados(Disciplina disciplina, String semestre) { //Conta quantos alunos matriculados na disciplina e semestre
    int quantidade = 0;

    for (Matricula matricula : listaGeral) {
        if (matricula.getDisciplina().getCodigo() == disciplina.getCodigo()
                && matricula.getSemestre().equals(semestre)) {
            quantidade++;
        }
    }

    return quantidade;
}

