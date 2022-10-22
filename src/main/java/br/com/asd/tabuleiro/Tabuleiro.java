package br.com.asd.tabuleiro;

import br.com.asd.navio.CategoriasNavio;
import br.com.asd.navio.Navio;

import java.util.Arrays;
import java.util.Set;

public class Tabuleiro {
    int tamanhoTabuleiro;

    String[][] tabuleiro;

    public Tabuleiro(int tamanhoTabuleiro) {
        this.tamanhoTabuleiro = tamanhoTabuleiro;
        this.tabuleiro = new String[tamanhoTabuleiro][tamanhoTabuleiro];

        preencherTabuleiro(tamanhoTabuleiro);

        for (String[]linha : tabuleiro) {
            System.out.println(Arrays.toString(linha));
        }
    }

    private void preencherTabuleiro(int tamanhoTabuleiro) {
        for (int i = 0; i < tamanhoTabuleiro; i++) {
            for (int j = 0; j < tamanhoTabuleiro; j++) {
                tabuleiro[i][j] = " ";
            }
        }

//        Esquadra do jogo
        Set<Navio> naviosAPosicionar = Set.of(
                new Navio("S1", CategoriasNavio.SUBMARINO),
                new Navio("S2", CategoriasNavio.SUBMARINO),
                new Navio("S3", CategoriasNavio.SUBMARINO),
                new Navio("C1", CategoriasNavio.CRUZADOR),
                new Navio("C2", CategoriasNavio.CRUZADOR),
                new Navio("P1", CategoriasNavio.PORTAAVIAO)
        );

        for (Navio navio : naviosAPosicionar) {
            int tamanhoNavio = navio.getTamanho();
            boolean barcoVertical = Math.random() < 0.5;

            boolean posicionamentoConcluido = false;

//            Tentar preencher o tabuleiro até encontrar uma posição válida
            while (!posicionamentoConcluido) {
                boolean colidiu = false;

                int linhaInicio = 0;
                int colunaInicio = 0;
                int linhaFim = 0;
                int colunaFim = 0;

//                Definir linhas de início e fim do barco aleatoriamente
                linhaInicio = (int) (Math.floor(Math.random() * (tamanhoTabuleiro - (barcoVertical ? tamanhoNavio : 1))));
                colunaInicio = (int) (Math.floor(Math.random() * (tamanhoTabuleiro - (barcoVertical ? 1 : tamanhoNavio))));
                linhaFim = linhaInicio + (barcoVertical ? tamanhoNavio : 1);
                colunaFim = colunaInicio + (barcoVertical ? 1 : tamanhoNavio);

//                Checagem de colisão
                for (int i = linhaInicio; i < linhaFim; i++) {
                    for (int j = colunaInicio; j < colunaFim; j++) {
                        if (tabuleiro[i][j] != " ") {
                            colidiu = true;
                        }
                    }
                }

//                Associar as posições encontradas ao navio
                for (int i = linhaInicio; i < linhaFim; i++) {
                    for (int j = colunaInicio; j < colunaFim; j++) {
//                        TODO: IMPLEMENTAR POSIÇÃO DO NAVIO
                    }
                }


//             Se não colidiu, preencher o tabuleiro com o valor correspondente ao barco
                if (!colidiu) {
                    for (int i = linhaInicio; i < linhaFim; i++) {
                        for (int j = colunaInicio; j < colunaFim; j++) {
                            tabuleiro[i][j] = navio.getNome();
                        }
                    }

                    posicionamentoConcluido = true;
                }


            }



        }

    }
}