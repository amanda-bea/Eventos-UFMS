package com.ufms.eventos.controller;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.services.RespostaFormularioService;


public class RespostaFormularioController {
    private RespostaFormularioService service;

    public RespostaFormularioController() {
        this.service = new RespostaFormularioService();
    }

    public boolean enviarResposta(RespostaFormularioDTO dto) {
        return service.salvarResposta(dto);
    }
    
}