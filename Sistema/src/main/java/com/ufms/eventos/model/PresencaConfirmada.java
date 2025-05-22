package com.ufms.eventos.model;

import lombok.Data;

@Data
public class PresencaConfirmada {
    private Usuario usuario;
    private Acao acao;

    public PresencaConfirmada(Usuario usuario, Acao acao) {
        this.usuario = usuario;
        this.acao = acao;
    }

}
