package com.ufms.eventos.repository;

import com.ufms.eventos.model.RespostaFormulario;
import java.util.List;

/**
 * Interface para repositório de respostas de formulário.
 */
public interface RespostaFormularioRepository {
    
    /**
     * Salva uma resposta de formulário.
     * @param resposta A resposta a ser salva.
     */
    void salvar(RespostaFormulario resposta);
    
    /**
     * Lista todas as respostas para uma ação específica.
     * @param acaoId O ID da ação.
     * @return Lista de respostas de formulário.
     */
    List<RespostaFormulario> listarPorAcaoId(Long acaoId);
    
}