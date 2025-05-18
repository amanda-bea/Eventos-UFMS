package com.ufms.eventos.controller;

import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.services.AdminService;
import com.ufms.eventos.services.EventoService;

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
    //public boolean editarSolicitacaoEvento(String nomeEvento, EventoDTO eventoDTO, Usuario usuario) {
    //    return eventoService.editarSolicitacaoEvento(nomeEvento, eventoDTO, usuario);
    //}

    // Exclui uma solicitação de evento (apenas se ainda não aprovado)
    //public boolean excluirSolicitacaoEvento(String nomeEvento, Usuario usuario) {
    //    return eventoService.excluirSolicitacaoEvento(nomeEvento, usuario);
    //}

    // Lista todos os eventos aguardando aprovação
    public void listarEventosAguardando() {
        adminService.listarEventosAguardando();
    }

    // Lista todas os eventos aprovados
    public void listarEventosAtivos() {
        adminService.listarEventosAtivos();
    }
    
}