package src;
public class Sala {
    private char bloco;
    private int numero;
    private int capacidade;

    public Sala(char bloco, int numero, int capacidade) {
        this.bloco = bloco;
        this.numero = numero;
        this.capacidade = capacidade;
    }

    public char getBloco() {
        return bloco;
    }

    public void setBloco(char bloco) {
        this.bloco = bloco;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
}