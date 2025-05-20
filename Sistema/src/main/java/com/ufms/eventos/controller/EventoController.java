package com.ufms.eventos.controller;

import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.services.AdminService;
import com.ufms.eventos.services.EventoService;
import java.util.List;

public class EventoController {

    private EventoService eventoService;
    private AdminService adminService;

    public EventoController() {
        this.eventoService = new EventoService();
    }

    // Solicita um novo evento (status: aguardando aprovação)
    public boolean solicitarEvento(EventoDTO eventoDTO, Organizador organizador) {
        return eventoService.solicitarEvento(eventoDTO, organizador);
    }

    // Aprova um evento (status: ativo)
    public boolean aprovarEvento(String nomeEvento) {
        return adminService.aprovarEvento(nomeEvento);
    }

    // Rejeita um evento (status: rejeitado)
    public boolean rejeitarEvento(String nomeEvento, String mensagemRejeicao) {
        return adminService.rejeitarEvento(nomeEvento, mensagemRejeicao);
    }

    // Edita uma solicitação de evento (apenas se ainda não aprovado)
    //public boolean editarSolicitacaoEvento(String nomeEvento, EventoDTO eventoDTO, Organizador organizador) {
    //    return eventoService.editarSolicitacaoEvento(nomeEvento, eventoDTO, organizador);
    //}

    // Exclui uma solicitação de evento (apenas se ainda não aprovado)
    //public boolean excluirSolicitacaoEvento(String nomeEvento, Organizador organizador) {
    //    return eventoService.excluirSolicitacaoEvento(nomeEvento, organizador);
    //}

    // Lista todos os eventos aguardando aprovação
    public List<EventoDTO> listarEventosAguardando() {
        return adminService.listarEventosAguardando();
    }

    // Lista todos os eventos aprovados
    public List<EventoDTO> listarEventosAtivos() {
        return adminService.listarEventosAtivos();
    }
}