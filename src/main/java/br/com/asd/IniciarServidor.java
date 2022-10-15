package br.com.asd;

import br.com.asd.servidor.BatalhaNavalServidor;

import java.net.SocketException;

public class IniciarServidor {
    public static void main(String[] args) {
        try {
            BatalhaNavalServidor servidor = new BatalhaNavalServidor();
            servidor.start();
            System.out.println("Servidor de Batalha Naval iniciado");

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}