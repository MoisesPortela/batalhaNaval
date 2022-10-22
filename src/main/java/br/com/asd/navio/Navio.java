package br.com.asd.navio;

import java.util.Set;

public class Navio {
    private int tamanho;

    private String nome;

    private CategoriasNavio categoria;

    private Set<PosicaoNavio> posicoes;

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

    public Set<PosicaoNavio> getPosicoes() {
        return posicoes;
    }

    public void setPosicoes(Set<PosicaoNavio> posicoes) {
        this.posicoes = posicoes;
    }

    public boolean isDestruido() {
        return destruido;
    }

    public void setDestruido(boolean destruido) {
        this.destruido = destruido;
    }


}
