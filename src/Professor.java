public class Professor extends Pessoa {
    private String departamento;

    public Professor(int id, String nome, String departamento) {
        super(id, nome);
        this.departamento = departamento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int contarDisciplinasMinistradas() {
        int contador = 0;
        for (Disciplina d : Disciplina.getListaGeral()) {
            if (d.getProfessor() != null && d.getProfessor().getId() == this.getId()) {
                contador++;
            }
        }
        return contador;
    }

    @Override
    public String getPapel() {
        return "Professor";
    }
}
