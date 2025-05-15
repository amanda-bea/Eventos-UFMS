package com.ufms.eventos.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Evento {
    private String nome;
    private LocalDate data; // Atualizado para LocalDate
    private String descricao;
    private String local;
    private LocalTime horarioInicio; // Atualizado para LocalTime
    private LocalTime horarioFim; // Atualizado para LocalTime
    private String organizador;
    private String departamento;
    private String contato; // Formato: telefone ou e-mail
    private String modalidade; // Presencial
    private String categoria; // Ex: Cultura, Educação, Saúde, etc.
    private String imagem; // URL da imagem do evento
    private String link; // Link para inscrição ou mais informações (opcional)
    private String status; // Ativo, Inativo, Cancelado, etc.
    private int capacidade; // Capacidade do evento (opcional)

    public Evento(String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio, LocalTime horarioFim, String organizador,
                  String departamento, String contato, String modalidade, String categoria, String imagem, String link, int capacidade) {
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
        this.local = local;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.organizador = organizador;
        this.departamento = departamento;
        this.contato = contato;
        this.modalidade = modalidade;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
        this.capacidade = capacidade;
    }

    public Evento(String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio, LocalTime horarioFim, String organizador, String contato,
                  String modalidade, String categoria, String imagem, String link, String status) {
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
        this.local = local;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.organizador = organizador;
        this.contato = contato;
        this.modalidade = modalidade;
        this.categoria = categoria;
        this.imagem = imagem;
        this.link = link;
        this.status = status;
    }

    public Evento(){}

    //status deve ser "Ativo", "Inativo", "Cancelado" de acordo com a data e se foi cancelado
    //pelo organizador ou não
    //ele também pode ser "aguardando aprovação", se foi aprovado também é ativo

    //metodos de inscrrição
    //confirmar inscrição/presença
    //cancelar inscrição/presença

    public void Inscricao() {
        // Lógica para inscrição no evento
        System.out.println("Inscrição realizada com sucesso para o evento: " + nome);
    }
}