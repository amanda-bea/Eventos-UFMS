package com.ufms.eventos.services;

import com.ufms.eventos.model.Evento;
import com.ufms.eventos.repository.EventoRepository;

public class AdminService {

    private EventoRepository er;

    public AdminService(EventoRepository er) {
        this.er = er;
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
    
    public void listarEventosAguardando() {
        for (Evento evento : er.getEventos()) {
            if (evento.getStatus().equals("Aguardando Aprovação")) {
                System.out.println(evento);
            }
        }
    }

    public void listarEventosAtivos() {
        for (Evento evento : er.getEventos()) {
            if (evento.getStatus().equals("Ativo")) {
                System.out.println(evento);
            }
        }
    }

}
