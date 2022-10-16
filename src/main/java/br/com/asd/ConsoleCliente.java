package br.com.asd;

import br.com.asd.cliente.BatalhaNavalCliente;
import br.com.asd.mensagens.MensagemConexao;

import java.net.SocketException;
import java.net.UnknownHostException;
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

    @Override
    public void run() {
        boolean conexaoEstabelecida = false;

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
                    } else {

                        throw new RuntimeException("Conexão não estabelecida");
                    }
                } catch (RuntimeException e) {
                    System.out.println("Não foi possível estabelecer conexão com o outro jogador.");
                    System.out.println("Tentando novamente em 5 segundos...");
                    Thread.sleep(5000);
                }
            }

//            Enviar ataques ao outro jogador
            while (true) {
                System.out.println("Digite o endereço de um ataque: ");
                String request = scanner.nextLine();
                String response = cliente.sendAtaque(request);
                System.out.println(response);
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
