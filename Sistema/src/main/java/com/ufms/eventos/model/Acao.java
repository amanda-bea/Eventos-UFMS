package com.ufms.eventos.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private String status; // Ativo, Inativo, Cancelado, etc.
    private int capacidade; // Capacidade do evento (opcional)

    //arrumar isso aqui depois
    public Acao(Evento evento, String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio, LocalTime horarioFim,
                Departamento departamento, String contato, String modalidade, String imagem, 
                String link, int capacidade, String status, String mensagemRejeicao) {
        this.evento = evento;
        // acima verficar se ação vai criar um evento com mesmos dados da ação
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
    }
    public Acao(){}

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


