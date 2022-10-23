package br.com.asd.tabuleiro;

import java.util.Arrays;

public class TabuleiroAtaque {
    int tamanhoTabuleiro;

    String[][] tabuleiroAtaque;

    public TabuleiroAtaque(int tamanhoTabuleiro) {
        this.tamanhoTabuleiro = tamanhoTabuleiro;
        this.tabuleiroAtaque = new String[tamanhoTabuleiro][tamanhoTabuleiro];

        for (int i = 0; i < tamanhoTabuleiro; i++) {
            for (int j = 0; j < tamanhoTabuleiro; j++) {
                tabuleiroAtaque[i][j] = " ";
            }
        }
    }

    public int getTamanhoTabuleiro() {
        return tamanhoTabuleiro;
    }

    public void setTamanhoTabuleiro(int tamanhoTabuleiro) {
        this.tamanhoTabuleiro = tamanhoTabuleiro;
    }

    public String[][] getTabuleiroAtaque() {
        return tabuleiroAtaque;
    }

    public void setTabuleiroAtaque(String[][] tabuleiroAtaque) {
        this.tabuleiroAtaque = tabuleiroAtaque;
    }

    public void setPosicaoAtacadaSucesso(int linha, int coluna) {
        tabuleiroAtaque[linha][coluna] = "X";
    }

    public void setPosicaoAtacadaFalha(int linha, int coluna) {
            tabuleiroAtaque[linha][coluna] = "A";
        }

    public void mostrarTabuleiro() {
        for (String[] linha : tabuleiroAtaque) {
            System.out.println(Arrays.toString(linha));
        }
    }
}
