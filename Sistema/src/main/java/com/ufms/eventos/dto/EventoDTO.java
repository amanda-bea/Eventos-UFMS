package com.ufms.eventos.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;
import lombok.Data;

@Data
public class EventoDTO {
    private String nome;
    private String data;
    private String descricao;
    private String local;
    private String  horarioInicio; // Atualizado para LocalTime
    private String  horarioFim; // Atualizado para LocalTime
    //private String  organizador; não deve aparecer no DTO
    private String departamento;
    private String contato; // Formato: telefone ou e-mail
    private String modalidade; // Presencial
    private String categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // URL da imagem do evento
    private String link; // Link para inscrição ou mais informações (opcional)
    //private String status; // Ativo, Inativo, Cancelado, etc. //não deve aparecer no DTO
    private int capacidade; // Capacidade do evento (opcional)

    public EventoDTO(String nome, String data, String descricao, String local, String  horarioInicio, String horarioFim, 
                     String departamento, String contato, String modalidade, String categoria, String imagem, String link, int capacidade) {
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
        this.local = local;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.departamento = departamento;
        this.contato = contato;
        this.modalidade = modalidade;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
        this.capacidade = capacidade;
    }

    public EventoDTO(String nome, String  data, String descricao, String local, String horarioInicio, String horarioFim, 
                     String contato, String modalidade, String categoria, String imagem, String link) {
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
        this.local = local;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.contato = contato;
        this.modalidade = modalidade;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
    }

    public EventoDTO(EventoDTO evento){
        this.nome = evento.getNome();
        this.data = evento.getData();
        this.descricao = evento.getDescricao();
        this.local = evento.getLocal();
        this.horarioInicio = evento.getHorarioInicio();
        this.horarioFim = evento.getHorarioFim();
        //this.organizador = evento.getOrganizador(); //não deve aparecer no DTO
        this.departamento = evento.getDepartamento();
        this.contato = evento.getContato();
        this.modalidade = evento.getModalidade();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }

    public EventoDTO(Evento evento) { // construtor de cópia
        this.nome = evento.getNome();
        this.descricao = evento.getDescricao();
        this.local = evento.getLocal();
        //tratar data e horarioInicio e horarioFim
        this.data = evento.getData().toString(); // converter LocalDate para String
        this.horarioInicio = evento.getHorarioInicio().toString(); // converter LocalTime para String
        this.horarioFim = evento.getHorarioFim().toString(); // converter LocalTime para String
        //this.organizador = evento.getOrganizador(); não sei se precisa
        this.departamento = evento.getDepartamento();
        this.contato = evento.getContato();
        this.modalidade = evento.getModalidade();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
        //this.status = evento.getStatus();
        this.capacidade = evento.getCapacidade();
    }

    public EventoDTO(){}

    public boolean validate() {
        // Implementar validações necessárias
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome do evento não pode ser vazio.");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data do evento não pode ser nula.");
        }
        if (descricao == null || descricao.isEmpty()) {
            throw new IllegalArgumentException("Descrição do evento não pode ser vazia.");
        }
        if (local == null || local.isEmpty()) {
            throw new IllegalArgumentException("Local do evento não pode ser vazio.");
        }
        if (horarioInicio == null || horarioFim == null) {
            throw new IllegalArgumentException("Horário de início e fim do evento não podem ser nulos.");
        }
        //tratar horarioInicio e horarioFim
        LocalTime horarioInicio = LocalTime.parse(this.horarioInicio);
        if (horarioInicio.isBefore(LocalTime.of(0, 0)) || horarioInicio.isAfter(LocalTime.of(23, 59))) {
            throw new IllegalArgumentException("Horário de início do evento deve estar entre 00:00 e 23:59.");
        }
        LocalTime horarioFim = LocalTime.parse(this.horarioFim);
        if (horarioFim.isBefore(LocalTime.of(0, 0)) || horarioFim.isAfter(LocalTime.of(23, 59))) {
            throw new IllegalArgumentException("Horário de fim do evento deve estar entre 00:00 e 23:59.");
        }
        if (horarioInicio.isAfter(horarioFim)) {
            throw new IllegalArgumentException("Horário de início não pode ser após o horário de fim.");
        }

        if (departamento == null || departamento.isEmpty()) {
            throw new IllegalArgumentException("Departamento do evento não pode ser vazio.");
        }
        //if (contato == null || contato.isEmpty()) {
        //    throw new IllegalArgumentException("Contato do evento não pode ser vazio.");
        //}
        if (modalidade == null || modalidade.isEmpty()) {
            throw new IllegalArgumentException("Modalidade do evento não pode ser vazia.");
        }
        if (categoria == null || categoria.isEmpty()) {
            throw new IllegalArgumentException("Categoria do evento não pode ser vazia.");
        }

        return true;
    }


}
