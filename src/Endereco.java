package src;
public class Endereco {
    private String rua;
    private int numero;
    private String cidade;
    private String uf;

    public Endereco(String rua, int numero, String cidade, String uf) {
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.uf = uf;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}