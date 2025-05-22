package com.ufms.eventos.controller;

import java.util.List;

import com.ufms.eventos.dto.EventoDTO;

import com.ufms.eventos.services.OrganizadorService;

import com.ufms.eventos.model.Organizador;

public class OrganizadorController {
    private OrganizadorService organizadorService;

    public OrganizadorController() {
        this.organizadorService = new OrganizadorService();
    }

    // Lista todos os eventos aguardando aprovação pro usuario tbm
    public List<EventoDTO> listarEventosAguardandoAprovacao(Organizador organizador) {
        return organizadorService.listarEventosAguardandoAprovacao(organizador);
    }

    public List<EventoDTO> listarEventosCancelados(Organizador organizador) {
        return organizadorService.listarEventosCancelados(organizador);
    }

    public List<EventoDTO> listarEventosAtivos(Organizador organizador) {
        return organizadorService.listarEventosAtivos(organizador);
    }

    public List<EventoDTO> listarEventosInativos(Organizador organizador) {
        return organizadorService.listarEventosInativos(organizador);
    }

    public List<EventoDTO> listarEventosPorOrganizador(Organizador organizador) {
        return organizadorService.listarEventosPorOrganizador(organizador);
    }
}
