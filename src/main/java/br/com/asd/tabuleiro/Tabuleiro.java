package br.com.asd.tabuleiro;

public class Tabuleiro {
    int tamanhoTabuleiro;

    char[][] tabuleiro;

    public Tabuleiro(int tamanhoTabuleiro) {
        this.tamanhoTabuleiro = tamanhoTabuleiro;
        this.tabuleiro = new char[tamanhoTabuleiro][tamanhoTabuleiro];
    }

}
