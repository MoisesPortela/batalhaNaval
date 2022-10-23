package br.com.asd.navio;


import java.util.ArrayList;
import java.util.List;

public class Navio {
    private int tamanho;

    private String nome;

    private CategoriasNavio categoria;

    private List<PosicaoNavio> posicoes;

    private boolean destruido;

    public Navio(String nome, CategoriasNavio categoria) {
        this.nome = nome;
        this.categoria = categoria;
        this.destruido = false;

        switch (categoria) {
            case SUBMARINO:
                this.tamanho = 1;
                break;
            case CRUZADOR:
                this.tamanho = 2;
                break;
            case PORTAAVIAO:
                this.tamanho = 3;
                break;
            default:
                this.tamanho = 0;
                break;
        }

        this.posicoes = new ArrayList<PosicaoNavio>();

    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CategoriasNavio getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriasNavio categoria) {
        this.categoria = categoria;
    }

    public List<PosicaoNavio> getPosicoes() {
        return posicoes;
    }

    public void setPosicoes(List<PosicaoNavio> posicoes) {
        this.posicoes = posicoes;
    }

    public boolean isDestruido() {
        return destruido;
    }

    public void setDestruido(boolean destruido) {
        this.destruido = destruido;
    }

    public void addPosicao(PosicaoNavio posicao) {
        this.posicoes.add(posicao);
    }

    public void receiveAttack(int linha, int coluna) {
        for (PosicaoNavio posicao : this.posicoes) {
            if (posicao.getLinha() == linha && posicao.getColuna() == coluna) {
                posicao.setAtingido(true);
            }
        }

        boolean destruido = true;

        for (PosicaoNavio posicao : this.posicoes) {
            if (!posicao.isAtingido()) {
                destruido = false;
                break;
            }
        }

        this.destruido = destruido;
    }

}
