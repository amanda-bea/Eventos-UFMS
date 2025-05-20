package com.ufms.eventos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;

import lombok.Data;

@Data
public class AcaoDTO {
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
    private String categoria;
    private String imagem;
    private String link;
    private String status;
    private String capacidade;

    public AcaoDTO(String evento, String nome, String data, String descricao, String local, String horarioInicio,
                   String horarioFim, String departamento, String contato, String modalidade, String categoria,
                   String imagem, String link, String status, String capacidade) {
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
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
        this.status = status;
        this.capacidade = capacidade;
    }

    public AcaoDTO(Evento evento, String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio,
                   LocalTime horarioFim, Organizador organizador, String departamento, String contato, String modalidade,
                   String categoria, String imagem, String link, int capacidade) {
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
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
        this.capacidade = Integer.toString(capacidade);
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
        this.categoria = acao.getCategoria();
        this.imagem = acao.getImagem();
        this.link = acao.getLink();
        this.status = acao.getStatus();
        this.capacidade = acao.getCapacidade();
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
        this.categoria = acao.getCategoria().name();
        this.imagem = acao.getImagem();
        this.link = acao.getLink();
        this.status = acao.getStatus();
        this.capacidade = Integer.toString(acao.getCapacidade());
    }

    public AcaoDTO() {
        // Construtor padrão
    }

    public boolean validate(){
        // Implementar validação dos campos
        if (nome == null || nome.isEmpty()) {
            return false;
        }
        if (data == null || data.isEmpty()) {
            return false;
        }
        if (descricao == null || descricao.isEmpty()) {
            return false;
        }
        if (local == null || local.isEmpty()) {
            return false;
        }
        if (horarioInicio == null || horarioInicio.isEmpty()) {
            return false;
        }
        if (horarioFim == null || horarioFim.isEmpty()) {
            return false;
        }
        if (departamento == null || departamento.isEmpty()) {
            return false;
        }
        if (contato == null || contato.isEmpty()) {
            return false;
        }
        if (modalidade == null || modalidade.isEmpty()) {
            return false;
        }
        if (categoria == null || categoria.isEmpty()) {
            return false;
        }
        if (imagem == null || imagem.isEmpty()) {
            return false;
        }
        if (link == null || link.isEmpty()) {
            return false;
        }
        if (status == null || status.isEmpty()) {
            return false;
        }
        if (capacidade == null || capacidade.isEmpty()) {
            return false;
        }
        return true;
    }
}
