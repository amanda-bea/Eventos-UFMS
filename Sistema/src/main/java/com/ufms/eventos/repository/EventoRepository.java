package com.ufms.eventos.repository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.time.LocalTime;
import java.time.LocalDate;

import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;

public class EventoRepository {
    private HashSet<Evento> eventos;

    public EventoRepository() {
        this.eventos = new HashSet<Evento>();
    }

    public HashSet<Evento> getEventos() {
        return new HashSet<Evento>(this.eventos); //retorna uma cópia do conjunto de eventos
    }

    public boolean addEvento(Evento evento) {
        return this.eventos.add(evento);
    }

    public boolean addEvento(String nome, String data, String descricao, String local, String horarioInicio,
                              String horarioFim, Organizador organizador, String departamento, String contato,
                              String modalidade, String categoria, String imagem, String link, int capacidade) {
        // tratar os dados de entrada
        // converter data, horarioInicio e horarioFim para os tipos apropriados
        LocalDate d = LocalDate.parse(data);
        LocalTime hi = LocalTime.parse(horarioInicio);
        LocalTime hf = LocalTime.parse(horarioFim);
        Evento evento = new Evento(nome, d, descricao, local, hi, hf, organizador, departamento, contato, modalidade, categoria, imagem, link, capacidade);
        return this.eventos.add(evento);
    }

    public boolean removeEvento(Evento evento) {
        return this.eventos.remove(evento);
    }

    public boolean updateEvento(Evento evento) { ///atualizar regra depois, apenas exemplo
        if (this.eventos.contains(evento)) {
            this.eventos.remove(evento);
            this.eventos.add(evento);
            return true;
        }
        return false;
    }

    public Evento getEvento(String nome){
        Iterator <Evento> iterator = this.eventos.iterator();
        
        Evento evento = null;
        while (iterator.hasNext()) {
            Evento e = iterator.next();
            if (e.getNome().equals(nome)) {
                evento = e;
                break;
            }
        }
        return evento;
    }
}
