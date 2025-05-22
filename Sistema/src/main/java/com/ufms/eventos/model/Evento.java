package com.ufms.eventos.model;

import lombok.Data;
import java.time.LocalDate;

import com.ufms.eventos.dto.EventoDTO;

@Data
public class Evento {
    private String nome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String descricao;
    private Organizador organizador;
    private Departamento departamento;
    private Categoria categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // URL da imagem do evento
    private String link; // Link para mais informações (opcional)
    private String status; // Ativo, Inativo, Cancelado, etc.
    private String mensagemRejeicao;

    public Evento(){}

    public Evento(String nome, LocalDate dataInicio, LocalDate dataFim, String descricao, Organizador organizador, Departamento departamento, 
                  Categoria categoria, String imagem, String link, String status, String mensagemRejeicao) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.descricao = descricao;
        this.organizador = organizador; // Presumindo que o organizador é um objeto do tipo Organizador, reeb nome do organzador como string
        this.departamento = departamento;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
        this.status = status;
        this.mensagemRejeicao = mensagemRejeicao;
    }

    public Evento(String nome, LocalDate dataInicio, LocalDate dataFim, String descricao, Organizador organizador, 
                  Departamento departamento, Categoria categoria, String imagem, String link) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.descricao = descricao;
        this.organizador = organizador;
        this.departamento = departamento;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
    }

    public Evento(EventoDTO evento) {
        this.nome = evento.getNome();
        this.dataInicio = LocalDate.parse(evento.getDataInicio());
        this.dataFim = LocalDate.parse(evento.getDataFim());
        this.descricao = evento.getDescricao();
        //this.organizador = evento.getOrganizador(); //não deve aparecer no DTO
        this.departamento = Departamento.valueOf(evento.getDepartamento());
        this.categoria = Categoria.valueOf(evento.getCategoria());
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return nome != null && nome.equalsIgnoreCase(evento.nome);
    }

    @Override
    public int hashCode() {
        return nome == null ? 0 : nome.toLowerCase().hashCode();
    }
    
}