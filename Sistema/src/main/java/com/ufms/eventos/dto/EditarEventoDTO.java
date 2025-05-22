package com.ufms.eventos.dto;

import lombok.Data;

import com.ufms.eventos.model.Evento;

@Data
public class EditarEventoDTO {
    private String nomeEvento;
    private String novaDataInicio;
    private String novaDataFim;
    private String novoLink;

    public EditarEventoDTO(String nomeEvento, String novaDataInicio, String novaDataFim, String novoLink) {
        this.nomeEvento = nomeEvento;
        this.novaDataInicio = novaDataInicio;
        this.novaDataFim = novaDataFim;
        this.novoLink = novoLink;
    }
    public EditarEventoDTO() {
    }
    public EditarEventoDTO(Evento evento) {
        this.nomeEvento = evento.getNome();
        this.novaDataInicio = evento.getDataInicio().toString();
        this.novaDataFim = evento.getDataFim().toString();
        this.novoLink = evento.getLink();
    }
    public EditarEventoDTO(EventoDTO eventoDTO) {
        this.nomeEvento = eventoDTO.getNome();
        this.novaDataInicio = eventoDTO.getDataInicio();
        this.novaDataFim = eventoDTO.getDataFim();
        this.novoLink = eventoDTO.getLink();
    }
}
