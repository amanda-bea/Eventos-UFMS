package com.ufms.eventos.dto;

import com.ufms.eventos.model.Evento;

import lombok.Data;

@Data
public class EventoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataFim;
    private String departamento;
    private String categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // path da imagem do evento
    private String link;
    private String status;
    private String organizador;

    public EventoDTO(String nome, String dataInicio, String dataFim, String descricao,String departamento, String categoria, 
                     String imagem, String link, String status, String organizador) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.descricao = descricao;
        this.departamento = departamento;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
        this.status = status;
        this.organizador = organizador;
    }

    public EventoDTO(Evento evento) {
        this.nome = evento.getNome();
        this.descricao = evento.getDescricao();
        this.dataInicio = evento.getDataInicio().toString();
        this.dataFim = evento.getDataFim().toString();
        this.status = evento.getStatus();
        this.departamento = evento.getDepartamento().name();
        this.categoria = evento.getCategoria().name();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
        this.organizador = evento.getOrganizador().getNome();
        this.id = evento.getId();
    }

    public EventoDTO(){}
}
