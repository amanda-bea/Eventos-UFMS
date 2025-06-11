package com.ufms.eventos.dto;

import com.ufms.eventos.model.Acao;
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
    }
}
