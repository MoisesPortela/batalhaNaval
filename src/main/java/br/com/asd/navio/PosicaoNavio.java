package br.com.asd.navio;

public class PosicaoNavio {
    int linha;
    int coluna;
    boolean atingido;

    public PosicaoNavio(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.atingido = false;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    public boolean isAtingido() {
        return atingido;
    }

    public void setAtingido(boolean atingido) {
        this.atingido = atingido;
    }
}
