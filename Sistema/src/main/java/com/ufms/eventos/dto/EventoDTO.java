package com.ufms.eventos.dto;

import com.ufms.eventos.model.Evento;

import lombok.Data;

@Data
public class EventoDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataFim;
    private String departamento;
    private String categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // URL da imagem do evento
    private String link;

    public EventoDTO(String nome, String dataInicio, String dataFim, String descricao,String departamento, String categoria, 
                     String imagem, String link) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.descricao = descricao;
        this.departamento = departamento;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
    }

    public EventoDTO(EventoDTO evento){
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio();
        this.dataFim = evento.getDataFim();
        this.descricao = evento.getDescricao();
        this.departamento = evento.getDepartamento();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }

    public EventoDTO(Evento evento) {
        this.nome = evento.getNome();
        this.descricao = evento.getDescricao();
        this.dataInicio = evento.getDataInicio().toString();
        this.dataFim = evento.getDataFim().toString();
        // Converte enums para String usando .name()
        this.departamento = evento.getDepartamento().name();
        this.categoria = evento.getCategoria().name();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }
    public EventoDTO(){}

    public EventoDTO(SolicitacaoEventoDTO evento) {
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio();
        this.dataFim = evento.getDataFim();
        this.descricao = evento.getDescricao();
        this.departamento = evento.getDepartamento();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }

    public boolean validate() {
        // Implementar validações necessárias
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome do evento não pode ser vazio.");
        }
        if (dataInicio == null) {
            throw new IllegalArgumentException("Data do evento não pode ser nula.");
        }
        if (dataFim == null) {
            throw new IllegalArgumentException("Data do evento não pode ser nula.");
        }
        if (descricao == null || descricao.isEmpty()) {
            throw new IllegalArgumentException("Descrição do evento não pode ser vazia.");
        }
        if (departamento == null || departamento.isEmpty()) {
            throw new IllegalArgumentException("Departamento do evento não pode ser vazio.");
        }
        if (categoria == null || categoria.isEmpty()) {
            throw new IllegalArgumentException("Categoria do evento não pode ser vazia.");
        }

        return true;
    }


}
