package br.com.fiap.techchallenge.quickserveapi.application.handler.interfaces;

public class ParametroBd {
    private String campo;
    private Object valor;

    public ParametroBd(String campo, Object valor) {
        this.campo = campo;
        this.valor = valor;
    }

    public String getCampo() {
        return campo;
    }

    public Object getValor() {
        return valor;
    }
}

