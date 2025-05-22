package com.ufms.eventos.dto;

import lombok.Data;

@Data
public class PresencaConfirmadaDTO {
    private String usuario;
    private String acao;

    public PresencaConfirmadaDTO(String usuario, String acao) {
        this.usuario = usuario;
        this.acao = acao;
    }

}
