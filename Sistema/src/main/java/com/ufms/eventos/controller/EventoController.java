package com.ufms.eventos.controller;

import com.ufms.eventos.model.Evento;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.EventoService;

import java.util.ArrayList;
import java.util.List;

public class EventoController {

    private List<Evento> eventos;
    private EventoService eventoService;

    public EventoController() {
        this.eventos = new ArrayList<>();
        this.eventoService = new EventoService();
    }

    public boolean addEvento(EventoDTO eventoDTO, Usuario usuario) {
        String nomeOrganizador = usuario.getNome(); // ou getId()
        return eventoService.addEvento(eventoDTO, nomeOrganizador);
    }

    public void addEvento(Evento evento) {
        eventos.add(evento);
    }

    public List<Evento> listarEventos() {
        return new ArrayList<>(eventos);
    }
}