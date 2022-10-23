package br.com.asd.servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.Map.Entry;
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

    public boolean enviarRespostaConexao(int tamanhoDefinidoTabuleiro, String quemDefiniuTamanhoTabuleiro) throws IOException {
        try {
            for (Entry<String, String> entry : portasJogadores.entrySet()) {
                Set<String> keysJogadores = new HashSet<>();
                String jogadorOponente = "";

                keysJogadores = portasJogadores.keySet();

                for (String key : keysJogadores) {
                    if (!key.equals(entry.getKey())) {
                        jogadorOponente = key;
                    }
                }

                String responseMsg = String.format("statusConnect: %s, tamanhoTabuleiro: %s, quemDefiniuTamanhoTabuleiro: %s, jogadorOponente: %s,", true, tamanhoDefinidoTabuleiro, quemDefiniuTamanhoTabuleiro, jogadorOponente);
                buf = responseMsg.getBytes();

                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), Integer.parseInt(entry.getValue()));
                System.out.println("Enviando resposta de conexão para " + entry.getKey() + " na porta " + entry.getValue());
                socket.send(packet);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void run() {
        running = true;
        boolean conexaoDoisJogadores = false;

        int tamanhoDefinidoTabuleiro = 0;
        String quemDefiniuTamanhoTabuleiro = "";

        Pattern extracaoNomeJogadorPattern = Pattern.compile("(?<=jogador: )(.*?)(?=,)");
        Pattern extracaoTamanhoTabuleiroPattern = Pattern.compile("(?<=tamanhoTabuleiro: )(.*?)(?=,)");
        Pattern extracaoTipoPattern = Pattern.compile("(?<=tipo: )(.*?)(?=,)");
        Pattern extracaoMovimentoPattern = Pattern.compile("(?<=movimento: )(.*?)(?=, alvo:)");
        Pattern extracaoAlvoPattern = Pattern.compile("(?<=alvo: )(.*?)(?=,)");

        while (running) {
//            Receber requisição
            buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
//                Receber requisição dos clientes
                socket.receive(packet);

//                Criar um matcher para extrair nome do jogador a partir da regex definica acima, em extracaoNomeJogadorPattern
                Matcher jogadorMatcher = extracaoNomeJogadorPattern.matcher(new String(packet.getData(), 0, packet.getLength()));

//                Incluir o nome do jogador que enviou a requisição no HashMap de portas de jogadores.
                if (jogadorMatcher.find()) {
                    System.out.println("Match do jogador: " + jogadorMatcher.group(0));

//                    Se o número de jogadores for menor ou igual a dois, incluir no HashMap de portas.
                    if (portasJogadores.size() <= 2) {
                        portasJogadores.put(jogadorMatcher.group(0), String.valueOf(packet.getPort()));
                    }
                }

//                Criar um matcher para extrair o tamanho do tabuleiro a partir da regex definica acima, em extracaoTamanhoTabuleiroPattern
//                Aqui, será definido o tamanho do tabuleiro. Se o tamanho do tabuleiro já tiver sido definido, não será possível definir novamente.
                if (tamanhoDefinidoTabuleiro == 0) {
                    Matcher tamanhoTabuleiroMatcher = extracaoTamanhoTabuleiroPattern.matcher(new String(packet.getData(), 0, packet.getLength()));

                    if (tamanhoTabuleiroMatcher.find()) {
                        tamanhoDefinidoTabuleiro = Integer.parseInt(tamanhoTabuleiroMatcher.group(0));
                        quemDefiniuTamanhoTabuleiro = jogadorMatcher.group(0);
                    }
                }

//                Verificar se o hash map possui dois jogadores, se sim, iniciar o jogo
                if (portasJogadores.size() == 2 && !conexaoDoisJogadores && tamanhoDefinidoTabuleiro != 0) {

                    System.out.println("Dois jogadores tentando se conectar.");
                    System.out.println("Enviando mensagem de conexão bem-sucedida aos jogadores.");
                    System.out.println("Tamanho do tabuleiro definido: " + tamanhoDefinidoTabuleiro + ". Quem definiu o tamanho do tabuleiro foi: " + quemDefiniuTamanhoTabuleiro);

                    conexaoDoisJogadores = enviarRespostaConexao(tamanhoDefinidoTabuleiro, quemDefiniuTamanhoTabuleiro);

                    if (conexaoDoisJogadores) {
                        System.out.println("Mensagem de conexão bem-sucedida enviada com sucesso.");
                    } else {
                        System.out.println("Erro ao enviar mensagem de conexão bem-sucedida.");
                    }

                }

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                System.out.println("Endereço:" + packet.getAddress());
                System.out.println("Porta:" + packet.getPort());

//                packet = new DatagramPacket(buf, buf.length, address, port);
//                Converter o pacote recebido em String
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);

//                Extrair o tipo de movimento e decidir o que fazer
                Matcher tipoMatcher = extracaoTipoPattern.matcher(received);
                Matcher movimentoMatcher = extracaoMovimentoPattern.matcher(received);
                Matcher alvoMatcher = extracaoAlvoPattern.matcher(received);

                if (tipoMatcher.find()) {
                    String tipo = tipoMatcher.group(0);
                    System.out.println("Tipo: " + tipo);

                    if (tipo.equals("ATTACK")) {

                        if (movimentoMatcher.find() && alvoMatcher.find()) {
                            String movimento = movimentoMatcher.group(0);
                            String alvo = alvoMatcher.group(0);

                            System.out.println("Movimento: " + movimento);
                            System.out.println("Alvo: " + alvo);

                            buf = received.getBytes();
                            int portaAlvo = Integer.parseInt(portasJogadores.get(alvo));

                            packet = new DatagramPacket(buf, buf.length, address, portaAlvo);
                            socket.send(packet);
                        }
                    } else if (tipo.equals("RESPOSTAATAQUE")) {
                        if (alvoMatcher.find()) {
                            System.out.println(received);
                            String alvo = alvoMatcher.group(0);

                            buf = received.getBytes();
                            int portaAlvo = Integer.parseInt(portasJogadores.get(alvo));

                            packet = new DatagramPacket(buf, buf.length, address, portaAlvo);
                            socket.send(packet);
                        }
                    }
                }

//                Encerrar a conexão
                if ("end".equals(received)) {
                    System.out.println("Conexão encerrada.");
                    running = false;
                    continue;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        socket.close();
    }

}
