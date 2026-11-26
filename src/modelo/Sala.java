package modelo;

public class Sala {
    private int id;
    private char bloco;
    private int numero;
    private int capacidade;

    public Sala(char bloco, int numero, int capacidade) {
        this(0, bloco, numero, capacidade);
    }

    public Sala(int id, char bloco, int numero, int capacidade) {
        this.id = id;
        this.bloco = bloco;
        this.numero = numero;
        this.capacidade = capacidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
