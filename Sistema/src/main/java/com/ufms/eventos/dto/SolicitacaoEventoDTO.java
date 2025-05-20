package com.ufms.eventos.dto;

import com.ufms.eventos.model.Evento;
import lombok.Data;

@Data
public class SolicitacaoEventoDTO {
    private String nome;
    private String dataInicio;
    private String dataFim;
    private String descricao;
    private String organizador;
    private String status;

    public SolicitacaoEventoDTO(Evento evento) {
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio().toString();
        this.dataFim = evento.getDataFim().toString();
        this.descricao = evento.getDescricao();
        this.organizador = evento.getOrganizador().getNome(); // supondo que Organizador tem getNome()
        this.status = evento.getStatus();
    }
}
