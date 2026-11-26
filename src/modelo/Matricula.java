package modelo;
public class Matricula {
    private int id;
    private Aluno aluno;
    private Disciplina disciplina;
    private double nota01;
    private double nota02;
    private double nota03;
    private double nota04;
    private double media;
    private String situacao;
    private String semestre;

    public Matricula(Aluno aluno, Disciplina disciplina, String semestre) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.semestre = semestre;
        this.situacao = "Cursando";
    }

    public void carregarNotasDoBanco(double n1, double n2, double n3, double n4, double media, String situacao) {
        this.nota01 = n1;
        this.nota02 = n2;
        this.nota03 = n3;
        this.nota04 = n4;
        this.media = media;
        this.situacao = situacao;
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

    public void setNotas(double n1, double n2, double n3, double n4) {
        this.nota01 = n1;
        this.nota02 = n2;
        this.nota03 = n3;
        this.nota04 = n4;
        this.calcularMedia();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setMedia(double media) {
        this.media = media;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }
}
