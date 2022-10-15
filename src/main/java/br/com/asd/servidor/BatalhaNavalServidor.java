package br.com.asd.servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatalhaNavalServidor extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    private HashMap<String, String> portasJogadores;

    public BatalhaNavalServidor() throws SocketException {
        socket = new DatagramSocket(4445);
        portasJogadores = new HashMap<>();
    }

    @Override
    public void run() {
        running = true;

        Pattern extracaoNomeJogadorPattern = Pattern.compile("(?<=jogador: )(.*)(?=,)");


        while (running) {
//            Receber requisição
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
                Matcher jogadorMatcher = extracaoNomeJogadorPattern.matcher(new String(packet.getData(), 0, packet.getLength()));
                MatchResult resultadoMatchJogador = jogadorMatcher.toMatchResult();

//                System.out.println(resultadoMatchJogador);
//                System.out.println(resultadoMatchJogador.start());
                System.out.println("Deu match?" + jogadorMatcher.find());
                System.out.println("Match start do nome do jogador:" + jogadorMatcher.start());
                System.out.println("Match end do nome do jogador:" + jogadorMatcher.end());
                System.out.println("Match do jogador:" + jogadorMatcher.group(0));

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                System.out.println("Endereço:" + packet.getAddress());
                System.out.println("Porta:" + packet.getPort());

//                packet = new DatagramPacket(buf, buf.length, address, port);

                String received = new String(packet.getData(), 0, packet.getLength());

                System.out.println(received);

                if ("end".equals(received)) {
                    System.out.println("Conexão encerrada.");
                    running = false;
                    continue;
                }

                socket.send(packet);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        socket.close();
    }

}
