package com.ufms.eventos.controller;

import com.ufms.eventos.model.Evento;

import java.util.ArrayList;
import java.util.List;

public class EventoController {

    private List<Evento> eventos;

    public EventoController() {
        this.eventos = new ArrayList<>();
    }

    public void adicionarEvento(Evento evento) {
        eventos.add(evento);
    }

    public List<Evento> listarEventos() {
        return new ArrayList<>(eventos);
    }
}
