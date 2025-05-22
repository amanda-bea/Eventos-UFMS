package com.ufms.eventos.controller;

import com.ufms.eventos.dto.EditarEventoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.EventoMinDTO;

import com.ufms.eventos.model.Organizador;

import com.ufms.eventos.services.EventoService;

import java.util.List;

public class EventoController {

    private EventoService eventoService;

    public EventoController() {
        this.eventoService = new EventoService();
    }

    public boolean solicitarEvento(EventoDTO eventoDTO, Organizador organizador) {
        return eventoService.solicitarEvento(eventoDTO, organizador);
    }

    public boolean excluirSolicitacaoEvento(String nomeEvento, Organizador organizador) {
        return eventoService.excluirSolicitacaoEvento(nomeEvento, organizador);
    }

    public boolean editarEvento(EditarEventoDTO dto, Organizador organizador) {
        return eventoService.editarEvento(dto, organizador);
    }

    public void atualizarEventosExpirados() {
        eventoService.atualizarEventosExpirados();
    }

    public boolean cancelarEvento(String nomeEvento, String motivo) {
        return eventoService.cancelarEvento(nomeEvento, motivo);
    }

    public List<EventoDTO> listarEventosAtivos() {
        return eventoService.listarEventosAtivos();
    }

    public List<EventoMinDTO> listarEventosAtivosMin() {
        return eventoService.listarEventosAtivosMin();
    }

    //public EventoDTO buscarEventoPorId(Long id) {
    //    return eventoService.buscarEventoPorId(id);
    //}
}
