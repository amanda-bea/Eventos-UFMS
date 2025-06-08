package com.ufms.eventos.repository;

import java.util.List;

import com.ufms.eventos.model.RespostaFormulario;

/**
 * Interface para padronizar as operações de repositório para a entidade RespostaFormulario.
 * Esta interface será implementada tanto por RespostaFormularioRepository (implementação em memória)
 * quanto por RespostaFormularioRepositoryJDBC (implementação com persistência em banco de dados).
 */
public interface IRespostaFormularioRepository {
    
    /**
     * Salva uma resposta de formulário.
     * @param resposta A resposta de formulário a ser salva.
     */
    public void salvar(RespostaFormulario resposta);

    /**
     * Lista todas as respostas de formulário associadas a uma ação específica.
     * @param acaoNome O nome da ação.
     * @return Uma lista de respostas de formulário.
     */
    public List<RespostaFormulario> listarPorAcao(String acaoNome);
}
