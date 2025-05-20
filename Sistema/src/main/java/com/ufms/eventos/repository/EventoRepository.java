package com.ufms.eventos.repository;

import java.time.LocalDate;
import java.util.HashSet;

import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;

public class EventoRepository {
    private HashSet<Evento> eventos;

    public EventoRepository() {
        this.eventos = new HashSet<>();
    }

    // Adiciona um novo evento ou solicitação
    public boolean addEvento(Evento evento) {
        return this.eventos.add(evento);
    }

    public boolean addEvento(String nome, LocalDate dataInicio, LocalDate dataFim, String descricao, Organizador organizador, 
                             String departamento, String categoria, String imagem, String link, String status, String mensagemRejeicao) {
        Evento evento = new Evento(nome, dataInicio, dataFim, descricao, organizador, departamento, categoria, imagem, link, status, mensagemRejeicao);
        return this.eventos.add(evento);
    }

    public boolean addEvento(String nome, LocalDate dataInicio, LocalDate dataFim, String descricao, Organizador organizador, 
                             String departamento, String categoria, String imagem, String link) {
        Evento evento = new Evento(nome, dataInicio, dataFim, descricao, organizador, departamento, categoria, imagem, link);
        return this.eventos.add(evento);
    }

    // Remove evento ou solicitação
    public boolean removeEvento(Evento evento) {
        return this.eventos.remove(evento);
    }

    // Atualiza evento ou solicitação
    public boolean updateEvento(Evento evento) {
        if (this.eventos.contains(evento)) {
            this.eventos.remove(evento);
            this.eventos.add(evento);
            return true;
        }
        return false;
    }

    // Busca evento por nome
    public Evento getEvento(String nome) {
        return this.eventos.stream()
            .filter(e -> e.getNome().equalsIgnoreCase(nome))
            .findFirst()
            .orElse(null);
    }

    // Lista todos os eventos
    public HashSet<Evento> getEventos() {
        return new HashSet<>(this.eventos);
    }

    // Lista apenas solicitações (status aguardando aprovação)
    public HashSet<Evento> getSolicitacoes() {
        HashSet<Evento> solicitacoes = new HashSet<>();
        for (Evento e : this.eventos) {
            if ("Aguardando aprovação".equalsIgnoreCase(e.getStatus())) {
                solicitacoes.add(e);
            }
        }
        return solicitacoes;
    }
}
