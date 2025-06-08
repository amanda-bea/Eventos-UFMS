package com.ufms.eventos.services;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.AdminRepositoryJDBC;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.IAdminRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de Serviço que contém a lógica de negócio EXCLUSIVA do Administrador.
 */
public class AdminService {

    private IAdminRepository adminRepository;
    private EventoRepository er;
    private AcaoRepository ar;

    public AdminService() {
        this.adminRepository = new AdminRepositoryJDBC();
        this.er = new EventoRepository();
        this.ar = new AcaoRepository();
    }

    /**
     * Busca todos os eventos com status "Aguardando aprovação".
     */
    public List<EventoMinDTO> listarEventosAguardando() {
        return er.getEventos().stream()
                .filter(e -> "Aguardando aprovação".equalsIgnoreCase(e.getStatus()))
                .map(EventoMinDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Aprova um evento, atualizando seu status e o de suas ações para "Ativo".
     */
    public boolean aprovarEvento(Long eventoId) {
        Evento evento = er.findById(eventoId);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            evento.setStatus("Ativo");
            er.updateEvento(evento);

            // Aprova também todas as ações associadas
            ar.getAcoes().stream()
                .filter(acao -> acao.getEvento().equals(evento))
                .forEach(acao -> {
                    acao.setStatus("Ativo");
                    ar.updateAcao(acao);
                });
            return true;
        }
        return false;
    }

    /**
     * Rejeita um evento, atualizando seu status e salvando o motivo.
     */
    public boolean rejeitarEvento(Long eventoId, String motivo) {
        Evento evento = er.findById(eventoId);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            evento.setStatus("Rejeitado");
            er.updateEvento(evento);

            // Rejeita também as ações associadas
            ar.getAcoes().stream()
                .filter(acao -> acao.getEvento().equals(evento))
                .forEach(acao -> {
                    acao.setStatus("Rejeitado");
                    acao.setMensagemRejeicao("O evento principal foi rejeitado.");
                    ar.updateAcao(acao);
                });
            return true;
        }
        return false;
    }

    /**
     * Cancela um evento. Esta é uma ação de admin.
     */
    public boolean cancelarEvento(String nomeEvento, String motivo) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null) {
            evento.setStatus("Cancelado");
            evento.setMensagemRejeicao(motivo);
            er.updateEvento(evento);
            return true;
        }
        return false;
    }
}
