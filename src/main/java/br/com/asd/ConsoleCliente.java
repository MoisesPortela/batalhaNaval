package br.com.asd;

import br.com.asd.cliente.BatalhaNavalCliente;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ConsoleCliente {
    public static int validarTamanhoTabuleiro() {
        while (true) {
            System.out.println("Digite o tamanho do tabuleiro: ");
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

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Digite o nome do jogador: ");
            String nome = scanner.nextLine();

            int tamanhoTabuleiro = validarTamanhoTabuleiro();

            BatalhaNavalCliente cliente = new BatalhaNavalCliente(nome, tamanhoTabuleiro);
            cliente.start();

            System.out.println("Novo cliente de Batalha Naval instanciado.");

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
}
