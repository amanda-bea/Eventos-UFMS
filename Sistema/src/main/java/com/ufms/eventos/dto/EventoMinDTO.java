package com.ufms.eventos.dto;

import com.ufms.eventos.model.Evento;

import lombok.Data;

@Data
public class EventoMinDTO {
    private String nome;
    private String dataInicio; //se data inicio for igual adata fim, mostrar apenas uma data
    private String categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // URL da imagem do evento

    public EventoMinDTO(String nome, String dataInicio, String categoria, String imagem) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.categoria = categoria;
        this.imagem = imagem;
    }

    public EventoMinDTO(EventoDTO evento){
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
    }

    public EventoMinDTO(Evento evento) {
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio().toString();
        this.categoria = evento.getCategoria().name();
        this.imagem = evento.getImagem();
    }
    public EventoMinDTO(){}

    public boolean validate() {
        // Implementar validações necessárias
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome do evento não pode ser vazio.");
        }
        if (dataInicio == null) {
            throw new IllegalArgumentException("Data do evento não pode ser nula.");
        }
        if (categoria == null || categoria.isEmpty()) {
            throw new IllegalArgumentException("Categoria do evento não pode ser vazia.");
        }

        return true;
    }


}
