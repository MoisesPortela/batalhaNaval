package br.com.asd.cliente;

import br.com.asd.mensagens.MensagemConexao;
import br.com.asd.tabuleiro.Tabuleiro;
import br.com.asd.tabuleiro.TabuleiroAtaque;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatalhaNavalCliente {

    private String nome;
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;

    private int tamanhoTabuleiro;

    private Tabuleiro tabuleiro;

    private TabuleiroAtaque tabuleiroAtaque;

    private int portaServidor;

    public BatalhaNavalCliente(String nome) throws SocketException, UnknownHostException {
        this.nome = nome;
        this.tamanhoTabuleiro = 10;
        this.tabuleiro = new Tabuleiro(tamanhoTabuleiro);
        this.tabuleiroAtaque = new TabuleiroAtaque(tamanhoTabuleiro);
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
        this.tabuleiroAtaque = new TabuleiroAtaque(tamanhoTabuleiro);
    }

    public void mostrarTabuleiro() {
        tabuleiro.mostrarTabuleiro();
    }

    public void mostrarTabuleiroAtaque() {
        tabuleiroAtaque.mostrarTabuleiro();
    }

    public HashMap<String, Integer> parseAttack(String attack) {
        Pattern pattern = Pattern.compile("([A-Z])([0-9]+)");
        Matcher matcher = pattern.matcher(attack);

        if (matcher.find()) {
            String letra = matcher.group(1);
            int numero = Integer.parseInt(matcher.group(2));

            int linha = letra.charAt(0) - 65;
            int coluna = numero - 1;

            return new HashMap<String, Integer>() {{
                put("coluna", coluna);
                put("linha", linha);
            }};
        }

        return null;
    }

    public HashMap<String, String> sendAtaque(String enderecoAtaque, String alvo) throws Exception {
//        Patterns para extração de dados
        Pattern extracaoNomeJogadorPattern = Pattern.compile("(?<=jogador: )(.*?)(?=,)");
        Pattern extracaoMovimentoPattern = Pattern.compile("(?<=movimento: )(.*)(?=, alvo: )");
        Pattern extracaoAlvoPattern = Pattern.compile("(?<=alvo: )(.*?)(?=,)");
        Pattern extracaoNavioAfundadoPattern = Pattern.compile("(?<=navioAfundado=)(.*?)(?=,)");
        Pattern extracaoNavioAtingidoPattern = Pattern.compile("(?<=navioAtingido=)(.*?)(?=,)");
        Pattern extracaoResultadoPattern = Pattern.compile("(?<=resultado=)(.*?)(?=,)");
        Pattern extracaoQuemGanhouPattern = Pattern.compile("(?<=quemGanhou=)(.*?)(?=,)");
        Pattern extracaoTipoNavioAtingidoPattern = Pattern.compile("(?<=tipoNavioAtingido=)(.*?)(?=,)");
        Pattern extracaoGameOverPattern = Pattern.compile("(?<=gameOver=)(.*?)(?=})");

//        HashMap com a linha e coluna das coornadas do ataque
        HashMap<String, Integer> parsedAttack = parseAttack(enderecoAtaque);

        String requestMsg = String.format("jogador: %s, tipo: %s, movimento: %s, alvo: %s,%n", nome, "ATTACK", parsedAttack, alvo);

        buf = requestMsg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portaServidor);

        socket.send(packet);

        buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);

        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());

//        Matcher para a extração de dados da resposta ao ataque
        Matcher extracaoNomeJogadorMatcher = extracaoNomeJogadorPattern.matcher(received);
        Matcher extracaoMovimentoMatcher = extracaoMovimentoPattern.matcher(received);
        Matcher extracaoAlvoMatcher = extracaoAlvoPattern.matcher(received);
        Matcher extracaoNavioAfundadoMatcher = extracaoNavioAfundadoPattern.matcher(received);
        Matcher extracaoNavioAtingidoMatcher = extracaoNavioAtingidoPattern.matcher(received);
        Matcher extracaoResultadoMatcher = extracaoResultadoPattern.matcher(received);
        Matcher extracaoQuemGanhouMatcher = extracaoQuemGanhouPattern.matcher(received);
        Matcher extracaoTipoNavioAtingidoMatcher = extracaoTipoNavioAtingidoPattern.matcher(received);
        Matcher extracaoGameOverMatcher = extracaoGameOverPattern.matcher(received);

//        Modelo de string a ser tratada:
//        jogador: Ricardo, tipo: RESPOSTAATAQUE, movimento: {navioAfundado=false, navioAtingido=null, resultado=agua, quemGanhou=null, tipoNavioAtingido=null, gameOver=false}, alvo: Moisés,

        if (extracaoNomeJogadorMatcher.find()
                && extracaoMovimentoMatcher.find()
                && extracaoAlvoMatcher.find()
                && extracaoNavioAfundadoMatcher.find()
                && extracaoNavioAtingidoMatcher.find()
                && extracaoResultadoMatcher.find()
                && extracaoQuemGanhouMatcher.find()
                && extracaoTipoNavioAtingidoMatcher.find()
                && extracaoGameOverMatcher.find()) {

            String nomeJogador = extracaoNomeJogadorMatcher.group(0);
            String movimento = extracaoMovimentoMatcher.group(0);
            String alvoResposta = extracaoAlvoMatcher.group(0);
            String navioAfundado = extracaoNavioAfundadoMatcher.group(0);
            String navioAtingido = extracaoNavioAtingidoMatcher.group(0);
            String resultado = extracaoResultadoMatcher.group(0);
            String quemGanhou = extracaoQuemGanhouMatcher.group(0);
            String tipoNavioAtingido = extracaoTipoNavioAtingidoMatcher.group(0);
            String gameOver = extracaoGameOverMatcher.group(0);

            HashMap<String, String> respostaAtaque = new HashMap<>() {{
                put("nomeJogador", nomeJogador);
                put("movimento", movimento);
                put("alvo", alvoResposta);
                put("navioAfundado", navioAfundado);
                put("navioAtingido", navioAtingido);
                put("resultado", resultado);
                put("quemGanhou", quemGanhou);
                put("tipoNavioAtingido", tipoNavioAtingido);
                put("gameOver", gameOver);
            }};

            if (respostaAtaque.get("resultado").equals("agua")) {
                this.tabuleiroAtaque.setPosicaoAtacadaFalha(parsedAttack.get("linha"), parsedAttack.get("coluna"));
            } else {
                this.tabuleiroAtaque.setPosicaoAtacadaSucesso(parsedAttack.get("linha"), parsedAttack.get("coluna"));
            }

            return respostaAtaque;
        }
        return null;
    }

    public HashMap<String, String> receiveAtaque() {


        buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = new String(packet.getData(), 0, packet.getLength());

//        Pattern para extrair o movimento do ataque e nome do jogador
        Pattern extracaoMovimentoPattern = Pattern.compile("(?<=movimento: )(.*)(?=, alvo: )");
        Pattern extracaoNomeJogadorPattern = Pattern.compile("(?<=jogador: )(.*?)(?=,)");
        Pattern extracaoAlvoPattern = Pattern.compile("(?<=alvo: )(.*?)(?=,)");
        Pattern extracaoLinhaPattern = Pattern.compile("(?<=linha=)(\\d+)");
        Pattern extracaoColunaPattern = Pattern.compile("(?<=coluna=)(\\d+)");

//        Matcher para extrair o movimento do ataque
        Matcher extracaoMovimentoMatcher = extracaoMovimentoPattern.matcher(received);
        Matcher extracaoNomeJogadorMatcher = extracaoNomeJogadorPattern.matcher(received);
        Matcher extracaoAlvoMatcher = extracaoAlvoPattern.matcher(received);
        Matcher extracaoLinhaMatcher = extracaoLinhaPattern.matcher(received);
        Matcher extracaoColunaMatcher = extracaoColunaPattern.matcher(received);

        if (extracaoNomeJogadorMatcher.find() && extracaoMovimentoMatcher.find() && extracaoAlvoMatcher.find()) {
            String jogadorAtacante = extracaoNomeJogadorMatcher.group(0);
            String movimento = extracaoMovimentoMatcher.group(0);

            if (extracaoLinhaMatcher.find() && extracaoColunaMatcher.find()) {
                int linha = Integer.parseInt(extracaoLinhaMatcher.group(0));
                int coluna = Integer.parseInt(extracaoColunaMatcher.group(0));

                HashMap<String, String> resultadoAtaque = tabuleiro.receiveAttack(linha, coluna);

                resultadoAtaque.put("quemGanhou", resultadoAtaque.get("gameOver").equals("true") ? jogadorAtacante : null);

                String attackResponseMessage = String.format("jogador: %s, tipo: %s, movimento: %s, alvo: %s,%n", nome, "RESPOSTAATAQUE", resultadoAtaque.toString(), jogadorAtacante);

                buf = attackResponseMessage.getBytes();

                packet = new DatagramPacket(buf, buf.length, address, portaServidor);

                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return resultadoAtaque;
            }

        }

        return null;
    }

    public void close() {
        socket.close();
    }

    public MensagemConexao conectarServidor(int tamanhoTabuleiro) throws IOException {
        String requestMsg = String.format("jogador: %s, tipo: %s, movimento: %s, tamanhoTabuleiro: %s,%n", nome, "CONNECT", "CONNECT", tamanhoTabuleiro);

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
        Pattern extracaoStatusConnectPattern = Pattern.compile("(?<=statusConnect: )(.*?)(?=,)");
        Pattern extracaoTamanhoTabuleiroPattern = Pattern.compile("(?<=tamanhoTabuleiro: )(.*?)(?=,)");
        Pattern extracaoQuemDefiniuTamanhoTabuleiroPattern = Pattern.compile("(?<=quemDefiniuTamanhoTabuleiro: )(.*?)(?=,)");
        Pattern extracaoJogadorOponentePattern = Pattern.compile("(?<=jogadorOponente: )(.*?)(?=,)");

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
