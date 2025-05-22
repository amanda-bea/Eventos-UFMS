package com.ufms.eventos.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

import com.ufms.eventos.dto.AcaoDTO;

@Data
public class Acao {
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
    private String imagem; // URL da imagem do evento
    private String link; // Link para mais informações (opcional)
    private String status; // Ativo, Inativo, Cancelado, Aguardando aprovação, Lotado, Rejeitado.
    private int capacidade; // Capacidade do evento (opcional)
    private String mensagemRejeicao; // Mensagem de rejeição (opcional)

    //arrumar isso aqui depois
    public Acao(Evento evento, String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio, LocalTime horarioFim,
                Departamento departamento, String contato, String modalidade, String imagem, 
                String link, int capacidade, String status, String mensagemRejeicao) {
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
        this.imagem = imagem;
        this.link = link;
        this.capacidade = capacidade;
        this.status = status;
        this.mensagemRejeicao = mensagemRejeicao;
    }

    public Acao(){
    }

    public Acao(AcaoDTO acaoDTO){
        this.nome = acaoDTO.getNome();
        this.data = LocalDate.parse(acaoDTO.getData());
        this.descricao = acaoDTO.getDescricao();
        this.local = acaoDTO.getLocal();
        this.horarioInicio = LocalTime.parse(acaoDTO.getHorarioInicio());
        this.horarioFim = LocalTime.parse(acaoDTO.getHorarioFim());
        this.departamento = Departamento.valueOf(acaoDTO.getDepartamento());
        this.contato = acaoDTO.getContato();
        this.modalidade = acaoDTO.getModalidade();
        this.imagem = acaoDTO.getImagem();
        this.link = acaoDTO.getLink();
        this.capacidade = Integer.parseInt(acaoDTO.getCapacidade());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Acao acao = (Acao) o;
        return nome != null && nome.equalsIgnoreCase(acao.nome);
    }

    @Override
    public int hashCode() {
        return nome == null ? 0 : nome.toLowerCase().hashCode();
    }

}


