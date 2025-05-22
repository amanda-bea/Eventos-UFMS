package com.ufms.eventos.services;

import com.ufms.eventos.dto.EventoDTO;
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

    public List<EventoDTO> listarEventosPorOrganizador(Organizador organizador) {
        HashSet<Evento> eventos = eventoRepository.getEventos();
        return eventos.stream()
                .filter(e -> e.getOrganizador().equals(organizador))
                .map(EventoDTO::new)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> listarEventosPorStatus(Organizador organizador, String status) {
        HashSet<Evento> eventos = eventoRepository.getEventos();
        return eventos.stream()
                .filter(e -> e.getOrganizador().equals(organizador))
                .filter(e -> e.getStatus().equalsIgnoreCase(status))
                .map(EventoDTO::new)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> listarEventosAguardandoAprovacao(Organizador organizador) {
        return listarEventosPorStatus(organizador, "Aguardando aprovação");
    }

    public List<EventoDTO> listarEventosCancelados(Organizador organizador) {
        return listarEventosPorStatus(organizador, "Cancelado");
    }

    public List<EventoDTO> listarEventosAtivos(Organizador organizador) {
        return listarEventosPorStatus(organizador, "Ativo");
    }

    public List<EventoDTO> listarEventosInativos(Organizador organizador) {
        return listarEventosPorStatus(organizador, "Inativo");
    }
}