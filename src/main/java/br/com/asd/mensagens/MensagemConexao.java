package br.com.asd.mensagens;

public class MensagemConexao {
    private boolean conexaoEstabelecida;
    private int tamanhoTabuleiro;

    private String quemDefiniuTamanhoTabuleiro;

    private String jogadorOponente;

    public MensagemConexao() {
        this.conexaoEstabelecida = false;
        this.tamanhoTabuleiro = 10;
        this.quemDefiniuTamanhoTabuleiro = "";
    }

    public boolean isConexaoEstabelecida() {
        return conexaoEstabelecida;
    }

    public void setConexaoEstabelecida(boolean conexaoEstabelecida) {
        this.conexaoEstabelecida = conexaoEstabelecida;
    }

    public int getTamanhoTabuleiro() {
        return tamanhoTabuleiro;
    }

    public void setTamanhoTabuleiro(int tamanhoTabuleiro) {
        this.tamanhoTabuleiro = tamanhoTabuleiro;
    }

    public String getQuemDefiniuTamanhoTabuleiro() {
        return quemDefiniuTamanhoTabuleiro;
    }

    public void setQuemDefiniuTamanhoTabuleiro(String quemDefiniuTamanhoTabuleiro) {
        this.quemDefiniuTamanhoTabuleiro = quemDefiniuTamanhoTabuleiro;
    }

    public String getJogadorOponente() {
        return jogadorOponente;
    }

    public void setJogadorOponente(String jogadorOponente) {
        this.jogadorOponente = jogadorOponente;
    }
}
