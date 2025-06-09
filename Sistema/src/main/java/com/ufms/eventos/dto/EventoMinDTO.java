package com.ufms.eventos.dto;

import com.ufms.eventos.model.Evento;

import lombok.Data;

@Data
public class EventoMinDTO {
    private Long id;
    private String nome;
    private String dataInicio;
    private String categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // URL da imagem do evento
    private String status;
    private String modalidade;

    public EventoMinDTO(Long id, String nome, String dataInicio, String categoria, String imagem, String status) {
        this.nome = nome;
        this.id = id;
        this.dataInicio = dataInicio;
        this.categoria = categoria;
        this.imagem = imagem;
        this.status = status;
    }

    public EventoMinDTO(EventoDTO evento){
        this.id = evento.getId();
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        
    }

    public EventoMinDTO(Evento evento) {
        this.id = evento.getId();
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio().toString();
        this.categoria = evento.getCategoria().name();
        this.imagem = evento.getImagem();
        this.status = evento.getStatus();
    }
    public EventoMinDTO(){}

}
