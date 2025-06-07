package com.ufms.eventos.services;

import com.ufms.eventos.dto.EventoMinDTO; // Usaremos EventoMinDTO para ser mais leve
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AdminService {

    private EventoRepository er;
    private AcaoRepository ar;

    public AdminService() {
        this.er = new EventoRepository();
        this.ar = new AcaoRepository(); // Precisa do repositório de ações também
    }

    /**
     * Busca todos os eventos com status "Aguardando aprovação".
     * @return Lista de DTOs dos eventos pendentes.
     */
    public List<EventoMinDTO> listarEventosAguardando() {
        return er.getEventos().stream()
                .filter(e -> "Aguardando aprovação".equalsIgnoreCase(e.getStatus()))
                .map(EventoMinDTO::new) // Usando DTO mínimo para a lista
                .collect(Collectors.toList());
    }

    /**
     * Aprova um evento, atualizando seu status para "Ativo".
     * @param nomeEvento O nome do evento a ser aprovado.
     * @return true se o evento foi encontrado e atualizado, false caso contrário.
     */
    public boolean aprovarEvento(String nomeEvento) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            evento.setStatus("Ativo");
            er.updateEvento(evento); // Supondo que seu repositório tenha este método para atualizar

            // Aprova também todas as ações associadas
            ar.getAcoes().stream()
                .filter(acao -> acao.getEvento().equals(evento))
                .forEach(acao -> {
                    acao.setStatus("Ativo");
                    // Se seu AcaoRepository tiver um método update, chame-o aqui.
                    // ar.updateAcao(acao);
                });
            return true;
        }
        return false;
    }

    /**
     * Rejeita um evento, atualizando seu status e salvando o motivo.
     * @param nomeEvento O nome do evento a ser rejeitado.
     * @param motivo A razão da rejeição.
     * @return true se o evento foi encontrado e atualizado, false caso contrário.
     */
    public boolean rejeitarEvento(String nomeEvento, String motivo) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            evento.setStatus("Rejeitado");
            evento.setMensagemRejeicao(motivo);
            er.updateEvento(evento);

            // Rejeita também as ações associadas
            ar.getAcoes().stream()
                .filter(acao -> acao.getEvento().equals(evento))
                .forEach(acao -> {
                    acao.setStatus("Rejeitado");
                    acao.setMensagemRejeicao("O evento principal foi rejeitado.");
                    // ar.updateAcao(acao);
                });
            return true;
        }
        return false;
    }

    // O método cancelarEvento pode permanecer aqui se for uma ação exclusiva de admin.
    public boolean cancelarEvento(String nomeEvento, String motivo) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null) {
            evento.setStatus("Cancelado");
            evento.setMensagemRejeicao(motivo); // Motivo do cancelamento
            er.updateEvento(evento);
            return true;
        }
        return false;
    }
}