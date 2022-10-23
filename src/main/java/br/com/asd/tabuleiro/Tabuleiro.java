package br.com.asd.tabuleiro;

import br.com.asd.navio.CategoriasNavio;
import br.com.asd.navio.Navio;
import br.com.asd.navio.PosicaoNavio;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Tabuleiro {
    int tamanhoTabuleiro;

    String[][] tabuleiro;

    Set<Navio> naviosJogo;

    public Tabuleiro(int tamanhoTabuleiro) {
        this.tamanhoTabuleiro = tamanhoTabuleiro;
        this.tabuleiro = new String[tamanhoTabuleiro][tamanhoTabuleiro];

        preencherTabuleiro(tamanhoTabuleiro);
    }

    private void preencherTabuleiro(int tamanhoTabuleiro) {
        for (int i = 0; i < tamanhoTabuleiro; i++) {
            for (int j = 0; j < tamanhoTabuleiro; j++) {
                tabuleiro[i][j] = " ";
            }
        }

//        Esquadra do jogo
        this.naviosJogo = Set.of(
                new Navio("S1", CategoriasNavio.SUBMARINO),
                new Navio("S2", CategoriasNavio.SUBMARINO),
                new Navio("S3", CategoriasNavio.SUBMARINO),
                new Navio("C1", CategoriasNavio.CRUZADOR),
                new Navio("C2", CategoriasNavio.CRUZADOR),
                new Navio("P1", CategoriasNavio.PORTAAVIAO)
        );

        for (Navio navio : this.naviosJogo) {
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

//             Se não colidiu, preencher o tabuleiro com o valor correspondente ao barco
                if (!colidiu) {
                    for (int i = linhaInicio; i < linhaFim; i++) {
                        for (int j = colunaInicio; j < colunaFim; j++) {
                            tabuleiro[i][j] = navio.getNome();
                            navio.addPosicao(new PosicaoNavio(i, j));
                        }
                    }

                    posicionamentoConcluido = true;
                }


            }


        }

    }

    public void mostrarTabuleiro() {
        for (String[] linha : tabuleiro) {
            System.out.println(Arrays.toString(linha));
        }
    }

    public HashMap<String, String> receiveAttack(Integer linha, Integer coluna) {
        String ataqueEfetuado = tabuleiro[linha][coluna];
        HashMap<String, String> resultadoAtaque = new HashMap<>();

//        Estrutura do HashMap
//        {
//          "resultado": "agua" | "acertou" | "afundou",
//          "navioAtingido": "S1" | "S2" | "S3" | "C1" | "C2" | "P1" | null,
//          "tipoNavioAtingido": "Submarino" | "Cruzador" | "PortaAviao" | null
//          "navioAfundado": true | false,
//          "gameOver": "true" | "false"
//        }

        if (ataqueEfetuado.equals(" ")) {
            tabuleiro[linha][coluna] = "A";

            resultadoAtaque.put("resultado", "agua");
            resultadoAtaque.put("navioAtingido", null);
            resultadoAtaque.put("tipoNavioAtingido", null);
            resultadoAtaque.put("navioAfundado", "false");
            resultadoAtaque.put("gameOver", "false");
        } else {
            String navioAtingidoString = tabuleiro[linha][coluna];
            tabuleiro[linha][coluna] = "X";

            Navio navioAtingido = naviosJogo.stream()
                    .filter(navio -> navio.getNome().equals(navioAtingidoString))
                    .findFirst()
                    .orElseThrow();

            navioAtingido.receiveAttack(linha, coluna);

            if (navioAtingido.isDestruido()) {
                System.out.printf("Seu oponente afundou o %s %s!%n", navioAtingido.getCategoria(), navioAtingido.getNome());
            }

            resultadoAtaque.put("resultado", navioAtingido.isDestruido() ? "afundou" : "acertou");
            resultadoAtaque.put("navioAtingido", navioAtingido.getNome());
            resultadoAtaque.put("tipoNavioAtingido", navioAtingido.getCategoria().toString());
            resultadoAtaque.put("navioAfundado", navioAtingido.isDestruido() ? "true" : "false");

//            Verificar o GameOver
            boolean gameOver = true;
            for (Navio navio : naviosJogo) {
                if (!navio.isDestruido()) {
                    gameOver = false;
                    break;
                }
            }
            resultadoAtaque.put("gameOver", gameOver ? "true" : "false");
        }

        return resultadoAtaque;
    }
}