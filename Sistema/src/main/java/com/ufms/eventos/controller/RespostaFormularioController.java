package com.ufms.eventos.controller;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.services.RespostaFormularioService;

public class RespostaFormularioController {
    private RespostaFormularioService service = new RespostaFormularioService();

    public void enviarResposta(RespostaFormularioDTO dto) {
        service.salvarResposta(dto);
    }
}
