package com.ufms.eventos.services;

import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.EventoRepository;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorService {

    private EventoRepository eventoRepository;

    public OrganizadorService() {
        this.eventoRepository = new EventoRepository();
    }

    // Lista todos os eventos criados por um organizador específico
    public List<Evento> listarEventosPorOrganizador(Organizador organizador) {
        HashSet<Evento> eventos = eventoRepository.getEventos();
        return eventos.stream()
                .filter(e -> e.getOrganizador().equals(organizador))
                .collect(Collectors.toList());
    }
}