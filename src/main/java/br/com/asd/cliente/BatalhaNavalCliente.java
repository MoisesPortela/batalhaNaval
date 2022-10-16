package br.com.asd.cliente;

import br.com.asd.mensagens.MensagemConexao;
import br.com.asd.tabuleiro.Tabuleiro;

import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatalhaNavalCliente {

    private String nome;
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;

    private int tamanhoTabuleiro;

    private Tabuleiro tabuleiro;

    private int portaServidor;

    public BatalhaNavalCliente(String nome) throws SocketException, UnknownHostException {
        this.nome = nome;
        this.tamanhoTabuleiro = 10;
        this.tabuleiro = new Tabuleiro(tamanhoTabuleiro);
        this.portaServidor = 4445;

        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public int getTamanhoTabuleiro() {
        return tamanhoTabuleiro;
    }

    public void setTamanhoTabuleiro(int tamanhoTabuleiro) {
        this.tamanhoTabuleiro = tamanhoTabuleiro;
        this.tabuleiro = new Tabuleiro(tamanhoTabuleiro);
    }

    public String sendAtaque(String inputMsg) throws Exception {
        String requestMsg = String.format("jogador: %s, tipo: %s, movimento: %s%n", nome, "ATTACK", inputMsg);

        buf = requestMsg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portaServidor);
        socket.send(packet);

        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());

        return received;
    }

    public void close() {
        socket.close();
    }

    public MensagemConexao conectarServidor(int tamanhoTabuleiro) throws IOException {
        String requestMsg = String.format("jogador: %s, tipo: %s, movimento: %s%n, tamanhoTabuleiro: %s", nome, "CONNECT", "CONNECT", tamanhoTabuleiro);

        buf = requestMsg.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portaServidor);
        socket.send(packet);

        buf = new byte[256];

        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());

//        String modelo que vem do servidor:
// statusConnect: %s, tamanhoTabuleiro: %s, quemDefiniuTamanhoTabuleiro: %s

//        Patterns para extrair os dados:
        Pattern extracaoStatusConnectPattern = Pattern.compile("(?<=statusConnect: )(.*)(?=, tamanhoTabuleiro: )");
        Pattern extracaoTamanhoTabuleiroPattern = Pattern.compile("(?<=tamanhoTabuleiro: )(.*)(?=, quemDefiniuTamanhoTabuleiro: )");
        Pattern extracaoQuemDefiniuTamanhoTabuleiroPattern = Pattern.compile("(?<=quemDefiniuTamanhoTabuleiro: )(.*)(?=, jogadorOponente: )");
        Pattern extracaoJogadorOponentePattern = Pattern.compile("(?<=jogadorOponente: )(.*)");

//        Matchers para extração de dados:
        Matcher extracaoStatusConnectMatcher = extracaoStatusConnectPattern.matcher(received);
        Matcher extracaoTamanhoTabuleiroMatcher = extracaoTamanhoTabuleiroPattern.matcher(received);
        Matcher extracaoQuemDefiniuTamanhoTabuleiroMatcher = extracaoQuemDefiniuTamanhoTabuleiroPattern.matcher(received);
        Matcher extracaoJogadorOponenteMatcher = extracaoJogadorOponentePattern.matcher(received);

//        Verificar se o pacote recebido corresponde ao formato desejado. Se sim, extrair os dados.
        if (extracaoStatusConnectMatcher.find() && extracaoTamanhoTabuleiroMatcher.find() && extracaoQuemDefiniuTamanhoTabuleiroMatcher.find() && extracaoJogadorOponenteMatcher.find()) {
            MensagemConexao mensagemConexao = new MensagemConexao();

            mensagemConexao.setConexaoEstabelecida(Boolean.parseBoolean(extracaoStatusConnectMatcher.group()));
            mensagemConexao.setTamanhoTabuleiro(Integer.parseInt(extracaoTamanhoTabuleiroMatcher.group()));
            mensagemConexao.setQuemDefiniuTamanhoTabuleiro(extracaoQuemDefiniuTamanhoTabuleiroMatcher.group());
            mensagemConexao.setJogadorOponente(extracaoJogadorOponenteMatcher.group());

            if (mensagemConexao.isConexaoEstabelecida()) {
                return mensagemConexao;

            } else {
                throw new RuntimeException("Erro ao conectar com o servidor.");
            }
        } else {
            throw new RuntimeException("Erro ao conectar com o servidor.");
        }

    }
}
