package com.ufms.eventos.model;

import java.time.LocalDateTime; // Usar LocalDateTime para TIMESTAMP
import lombok.Data;

@Data
public class PresencaConfirmada {
    private Integer id; // Chave primária da tabela
    private Usuario usuario;
    private Acao acao;
    private LocalDateTime dataConfirmacao; // Campo para a coluna data_confirmacao

    public PresencaConfirmada(Usuario usuario, Acao acao) {
        this.usuario = usuario;
        this.acao = acao;
    }
}