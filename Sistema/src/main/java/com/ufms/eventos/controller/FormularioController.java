package com.ufms.eventos.controller;

import com.ufms.eventos.dto.FormularioDTO;
import com.ufms.eventos.services.FormularioService;

public class FormularioController {

    private FormularioService service;

    // Método para salvar o formulário, recebendo os dados da View
    //public Formulario salvarFormulario(FormularioDTO dto) {
    //    return service.salvarFormulario(dto);
    //}

    // Se precisar, método para validar dados antes de salvar (exemplo)
    public boolean validarFormulario(FormularioDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            return false;
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            return false;
        }
        // Outras validações
        return true;
    }
}
