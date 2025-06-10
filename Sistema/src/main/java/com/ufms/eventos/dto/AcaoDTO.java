package com.ufms.eventos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;

import lombok.Data;

@Data
public class AcaoDTO {
    private Long id;
    private String evento;
    private String nome;
    private String data;
    private String descricao;
    private String local;
    private String horarioInicio;
    private String horarioFim;
    private String departamento;
    private String contato; // Formato: telefone ou e-mail
    private String modalidade;
    private String link;
    private String capacidade;
    private String avisoVagas;
    private String status;

    public AcaoDTO(Long id, String evento, String nome, String data, String descricao, String local, String horarioInicio,
                   String horarioFim, String departamento, String contato, String modalidade,
                   String imagem, String link, String capacidade) {
        this.evento = evento;
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
        this.local = local;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.departamento = departamento;
        this.contato = contato;
        this.modalidade = modalidade;
        this.link = link;
        this.capacidade = capacidade;
        this.id = id;
    }

    public AcaoDTO(Long id, Evento evento, String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio,
                   LocalTime horarioFim, Organizador organizador, String departamento, String contato, String modalidade, 
                   String imagem, String link, int capacidade) {
        this.evento = evento.getNome();
        this.nome = nome;
        this.data = data.toString();
        this.descricao = descricao;
        this.local = local;
        this.horarioInicio = horarioInicio.toString();
        this.horarioFim = horarioFim.toString();
        this.departamento = departamento;
        this.contato = contato;
        this.modalidade = modalidade;
        this.link = link;
        this.capacidade = Integer.toString(capacidade);
        this.id = id;
    }

    public AcaoDTO(AcaoDTO acao) {
        this.evento = acao.getEvento();
        this.nome = acao.getNome();
        this.data = acao.getData();
        this.descricao = acao.getDescricao();
        this.local = acao.getLocal();
        this.horarioInicio = acao.getHorarioInicio();
        this.horarioFim = acao.getHorarioFim();
        this.departamento = acao.getDepartamento();
        this.contato = acao.getContato();
        this.modalidade = acao.getModalidade();
        this.link = acao.getLink();
        this.capacidade = acao.getCapacidade();
        this.id = acao.getId();
    }

    public AcaoDTO(Acao acao) {
        this.evento = acao.getEvento().getNome();
        this.nome = acao.getNome();
        this.data = acao.getData().toString();
        this.descricao = acao.getDescricao();
        this.local = acao.getLocal();
        this.horarioInicio = acao.getHorarioInicio().toString();
        this.horarioFim = acao.getHorarioFim().toString();
        this.departamento = acao.getDepartamento().name();
        this.contato = acao.getContato();
        this.modalidade = acao.getModalidade();
        this.link = acao.getLink();
        this.capacidade = Integer.toString(acao.getCapacidade());
        this.id = acao.getId();
        this.status = acao.getStatus();
    }

    public AcaoDTO() {
        // Construtor padrão
    }
}
