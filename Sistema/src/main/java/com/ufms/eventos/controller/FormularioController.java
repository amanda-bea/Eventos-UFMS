package com.ufms.eventos.controller;

import com.ufms.eventos.dto.FormularioDTO;
import com.ufms.eventos.services.FormularioService;

public class FormularioController {

    private FormularioService service;

    public FormularioController() {
        this.service = new FormularioService();
    }

    //public FormularioDTO criarFormulario(FormularioDTO formularioDTO) {
    //    return service.criarFormulario(formularioDTO);
    //}

    
}
