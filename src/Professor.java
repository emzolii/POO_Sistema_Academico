package src;
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

    @Override
    public String getPapel() {
        return "Professor";
    }
}