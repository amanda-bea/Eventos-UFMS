package com.ufms.eventos.controller;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.PresencaConfirmadaService;
import java.util.List;

public class PresencaConfirmadaController {
    private PresencaConfirmadaService service;

    public PresencaConfirmadaController() {
        this.service = new PresencaConfirmadaService();
    }

    public List<EventoMinDTO> listarEventosComPresencaConfirmada(Usuario usuario) {
        return service.listarEventosComPresencaConfirmada(usuario);
    }
}