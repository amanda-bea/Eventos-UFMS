package com.ufms.eventos.repository;

import java.util.HashSet;
import java.util.Iterator;

import com.ufms.eventos.model.Evento;

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
