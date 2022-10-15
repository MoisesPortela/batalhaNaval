package br.com.asd.cliente;

import br.com.asd.tabuleiro.Tabuleiro;

import java.net.*;

public class BatalhaNavalCliente extends Thread {

    private String nome;
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;

    private int tamanhoTabuleiro;

    private Tabuleiro tabuleiro;

    public BatalhaNavalCliente(String nome, int tamanhoTabuleiro) throws SocketException, UnknownHostException {
        this.nome = nome;
        this.tamanhoTabuleiro = tamanhoTabuleiro;
        this.tabuleiro = new Tabuleiro(tamanhoTabuleiro);

        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendAtaque(String inputMsg) throws Exception {
        String requestMsg = String.format("jogador: %s, movimento: %s%n", nome, inputMsg);

        buf = requestMsg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);

        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());

        return received;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("Ataque recebido em: " + new String(packet.getData(), 0, packet.getLength()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        socket.close();
    }
}
