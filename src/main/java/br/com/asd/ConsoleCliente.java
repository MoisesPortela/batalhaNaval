package br.com.asd;

import br.com.asd.cliente.BatalhaNavalCliente;
import br.com.asd.mensagens.MensagemConexao;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class ConsoleCliente extends Thread {
    public static int validarTamanhoTabuleiro() {
        while (true) {
            System.out.println("Digite o tamanho desejado tabuleiro: ");
            Scanner scanner = new Scanner(System.in);
            String tamanhoTabuleiro = scanner.nextLine();

            try {
                int tamanho = Integer.parseInt(tamanhoTabuleiro);

                if (tamanho < 10) {
                    System.out.println("O tamanho do tabuleiro deve ser maior que 10.");
                    continue;
                } else if (tamanho > 20) {
                    System.out.println("O tamanho do tabuleiro deve ser menor que 20.");
                    continue;
                }

                return tamanho;
            } catch (NumberFormatException e) {
                System.out.println("Tamanho inválido");
            }
        }
    }

    public void computarGameOver(String nome, String quemGanhou) {
        if (quemGanhou.equals(nome)) {
            System.out.println("Você ganhou a batalha naval!");
        } else {
            System.out.println("O jogador " + quemGanhou + " ganhou!");
        }
    }

    @Override
    public void run() {
        boolean conexaoEstabelecida = false;
        boolean gameOver = false;
        boolean meuTurno = false;
        String quemGanhou = "";

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Digite o nome do jogador: ");
            String nome = scanner.nextLine();

            int tamanhoTabuleiro = validarTamanhoTabuleiro();

            BatalhaNavalCliente cliente = new BatalhaNavalCliente(nome);

            System.out.println("Novo cliente de Batalha Naval instanciado.");

            MensagemConexao mensagemConexao = new MensagemConexao();

//            Tentar estabelecer conexão com o outro jogador. A sessão de jogo somente iniciará com dois jogadores.
            while (!conexaoEstabelecida) {
                System.out.println("Tentando estabelecer conexão com o outro jogador...");
                try {
                    mensagemConexao = cliente.conectarServidor(tamanhoTabuleiro);
                    conexaoEstabelecida = mensagemConexao.isConexaoEstabelecida();

                    if (conexaoEstabelecida) {
                        System.out.println("Conexão estabelecida com o outro jogador.");
                        System.out.println("O tamanho do tabuleiro definido foi: " + mensagemConexao.getTamanhoTabuleiro());
                        System.out.println("O jogador que definiu o tamanho do tabuleiro foi: " + mensagemConexao.getQuemDefiniuTamanhoTabuleiro());
                        System.out.println("O jogador oponente é: " + mensagemConexao.getJogadorOponente());
                        System.out.println("O primeiro jogador a atacar é: " + mensagemConexao.getQuemDefiniuTamanhoTabuleiro());

//                        Definir de quem será o primeiro turno para o ataque
                        if (mensagemConexao.getQuemDefiniuTamanhoTabuleiro().equals(nome)) {
                            meuTurno = true;
                        }
                    } else {

                        throw new RuntimeException("Conexão não estabelecida");
                    }
                } catch (RuntimeException e) {
                    System.out.println("Não foi possível estabelecer conexão com o outro jogador.");
                    System.out.println("Tentando novamente em 5 segundos...");
                    Thread.sleep(5000);
                }
            }

//            Atualizar o tabuleiro, após estabelecida a conexão com o outro jogador.
            cliente.setTamanhoTabuleiro(mensagemConexao.getTamanhoTabuleiro());

            while (!gameOver) {
//               Se for o meu turno, solicitar a posição para atacar.
                HashMap<String, String> response = new HashMap<>();
                if (meuTurno) {
                    System.out.printf("Digite o endereço de um ataque ao jogador %s: ", mensagemConexao.getJogadorOponente());
                    String request = scanner.nextLine();
                    response = cliente.sendAtaque(request, mensagemConexao.getJogadorOponente());

//                    Mostrar os resultados do meu ataque
                    if (response.get("resultado").equals("agua")) {
                        System.out.println("Seu tiro foi na água!");
                        meuTurno = false;
                    } else {
                        if (response.get("navioAfundado").equals("true")) {
                            System.out.printf("Você afundou o %s %s do jogador %s!", response.get("navioAtingido"), response.get("tipoNavioAtingido"), mensagemConexao.getJogadorOponente());
                        } else {
                            System.out.printf("Você atingiu o %s %s do jogador %s!", response.get("navioAtingido"), response.get("tipoNavioAtingido"), mensagemConexao.getJogadorOponente());
                        }
                    }

//                    Mostrar o tabuleiro de ataque
                    System.out.println("Seu tabuleiro de ataque: ");
                    cliente.mostrarTabuleiroAtaque();
                } else {
//                    Se não for o meu turno, aguardar o ataque do outro jogador.
                    System.out.println("Aguardando o ataque do outro jogador...");
                    response = cliente.receiveAtaque();

//                    Mostrar os resultados do ataque do outro jogador
                    if (response.get("resultado").equals("agua")) {
                        System.out.printf("O tiro do seu oponente %s caiu na água!", mensagemConexao.getJogadorOponente());
                        meuTurno = true;
                    } else {
                        if (response.get("navioAfundado").equals("true")) {
                            System.out.printf("Seu oponente %s afundou o %s %s!", mensagemConexao.getJogadorOponente(), response.get("tipoNavioAtingido"), response.get("navioAtingido"));
                        } else {
                            System.out.printf("Seu oponente %s atingiu o %s %s!", mensagemConexao.getJogadorOponente(), response.get("tipoNavioAtingido"), response.get("navioAtingido"));
                        }
                    }

//                    Mostrar o seu tabuleiro
                    System.out.println("Seu tabuleiro: ");
                    cliente.mostrarTabuleiro();


                }

                //                   Computar se foi Game Over
                if (response.get("gameOver").equals("true")) {
                    gameOver = true;
                    quemGanhou = response.get("quemGanhou");
                    computarGameOver(nome, quemGanhou);
                }
            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ConsoleCliente consoleCliente = new ConsoleCliente();
        consoleCliente.start();
    }
}
