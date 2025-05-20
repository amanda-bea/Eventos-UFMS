package com.ufms.eventos.dto;

import com.ufms.eventos.model.Evento;
import lombok.Data;

@Data
public class EventoDTO {
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataFim;
    //private String  organizador; não deve aparecer no DTO
    private String departamento;
    private String categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // URL da imagem do evento
    private String link; // Link para inscrição ou mais informações (opcional)
    //private String status; // Ativo, Inativo, Cancelado, etc. //não deve aparecer no DTO

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
        //this.organizador = evento.getOrganizador(); //não deve aparecer no DTO
        this.departamento = evento.getDepartamento();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }

    public EventoDTO(Evento evento) { // construtor de cópia
        this.nome = evento.getNome();
        this.descricao = evento.getDescricao();
        //tratar data e horarioInicio e horarioFim
        this.dataInicio = evento.getDataInicio().toString(); // converter LocalDate para String
        this.dataFim = evento.getDataFim().toString(); // converter LocalDate para String
        //this.organizador = evento.getOrganizador(); não sei se precisa
        this.departamento = evento.getDepartamento();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
        //this.status = evento.getStatus();
    }

    public EventoDTO(){}

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
