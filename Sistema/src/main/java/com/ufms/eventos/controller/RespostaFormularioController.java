package com.ufms.eventos.controller;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.services.RespostaFormularioService;

/**
 * Controller para gerenciar respostas de formulários
 */
public class RespostaFormularioController {
    private RespostaFormularioService service;

    /**
     * Construtor padrão
     */
    public RespostaFormularioController() {
        this.service = new RespostaFormularioService();
    }

    /**
     * Envia uma resposta de formulário para uma ação
     * @param dto Dados do formulário preenchido
     * @return true se a resposta foi salva com sucesso
     */
    public boolean enviarResposta(RespostaFormularioDTO dto) {
        return service.salvarResposta(dto);
    }
    
}