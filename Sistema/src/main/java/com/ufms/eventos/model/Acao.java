package com.ufms.eventos.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

import com.ufms.eventos.dto.AcaoDTO;

@Data
public class Acao {
    private Long id; // Adicionado id
    private Evento evento;
    private String nome;
    private LocalDate data;
    private String descricao;
    private String local;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private Departamento departamento;
    private String contato; // Formato: telefone ou e-mail
    private String modalidade; // Presencial
    private String link; // Link para mais informações (opcional)
    private String status; // Ativo, Inativo, Cancelado, Aguardando aprovação, Lotado, Rejeitado.
    private int capacidade; // Capacidade do evento (opcional)
    private String mensagemRejeicao; // Mensagem de rejeição (opcional)

    public Acao() {
    }

    public Acao(AcaoDTO acaoDTO) {
        this.nome = acaoDTO.getNome();
        this.data = LocalDate.parse(acaoDTO.getData());
        this.descricao = acaoDTO.getDescricao();
        this.local = acaoDTO.getLocal();
        this.horarioInicio = LocalTime.parse(acaoDTO.getHorarioInicio());
        this.horarioFim = LocalTime.parse(acaoDTO.getHorarioFim());
        this.departamento = Departamento.valueOf(acaoDTO.getDepartamento());
        this.contato = acaoDTO.getContato();
        this.modalidade = acaoDTO.getModalidade();
        this.link = acaoDTO.getLink();
        this.capacidade = Integer.parseInt(acaoDTO.getCapacidade());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Acao acao = (Acao) o;
        return id != null && id.equals(acao.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}