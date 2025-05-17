package com.ufms.eventos.dto;

import com.ufms.eventos.model.Evento;
import lombok.Data;

@Data
public class SolicitacaoEventoDTO {
    private String nome;
    private String data;
    private String descricao;
    private String organizador;
    private String status;

    public SolicitacaoEventoDTO(Evento evento) {
        this.nome = evento.getNome();
        this.data = evento.getData().toString();
        this.descricao = evento.getDescricao();
        this.organizador = evento.getOrganizador().getNome(); // supondo que Organizador tem getNome()
        this.status = evento.getStatus();
    }
}
