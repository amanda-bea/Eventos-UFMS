package com.ufms.eventos.controller;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.model.RespostaFormulario;
import com.ufms.eventos.services.RespostaFormularioService;

import java.util.List;

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
    
    /**
     * Lista todas as respostas para uma ação específica
     * @param nomeAcao Nome da ação
     * @return Lista de respostas
     */
    public List<RespostaFormulario> listarRespostas(String nomeAcao) {
        return service.listarRespostasPorAcao(nomeAcao);
    }
    
    /**
     * Lista todas as respostas para uma ação usando seu ID
     * @param acaoId ID da ação
     * @return Lista de respostas
     */
    public List<RespostaFormulario> listarRespostasPorId(Long acaoId) {
        return service.listarRespostasPorAcaoId(acaoId);
    }
    
}