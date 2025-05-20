package com.ufms.eventos.services;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.repository.EventoRepository;

public class AdminService {

    private EventoRepository er;

    public AdminService() {
        this.er = new EventoRepository();
    }

    public boolean aprovarEvento(String nomeEvento) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento == null) {
            throw new RuntimeException("Evento não encontrado");
        }
        evento.setStatus("Ativo");
        return true;
    }

    public boolean rejeitarEvento(String nomeEvento, String motivo) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento == null) {
            throw new RuntimeException("Evento não encontrado");
        }
        evento.setStatus("Rejeitado");
        evento.setMensagemRejeicao(motivo);
        return true;
    }

    public boolean cancelarEvento(String nomeEvento, String motivo) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento == null) {
            throw new RuntimeException("Evento não encontrado");
        }
        evento.setStatus("Cancelado");
        evento.setMensagemRejeicao(motivo);
        return true;
    }
    
    public List<EventoDTO> listarEventosAguardando() {
        HashSet<Evento> eventos = er.getEventos();
        return eventos.stream()
                .filter(e -> "Aguardando aprovação".equalsIgnoreCase(e.getStatus()))
                .map(EventoDTO::new)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> listarEventosAtivos() {
        HashSet<Evento> eventos = er.getEventos();
        return eventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .map(EventoDTO::new)
                .collect(Collectors.toList());
    }

}
