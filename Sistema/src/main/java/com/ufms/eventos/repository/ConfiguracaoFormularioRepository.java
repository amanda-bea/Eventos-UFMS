package com.ufms.eventos.repository;

import com.ufms.eventos.model.ConfiguracaoFormulario;
import java.util.Optional;

/**
 * Interface para repositório de configurações de formulário.
 */
public interface ConfiguracaoFormularioRepository {

    /**
     * Salva ou atualiza uma configuração de formulário.
     * @param configuracao O objeto ConfiguracaoFormulario a ser salvo.
     * @return O objeto ConfiguracaoFormulario salvo.
     */
    ConfiguracaoFormulario salvar(ConfiguracaoFormulario configuracao);

    /**
     * Busca uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return Um Optional contendo o ConfiguracaoFormulario se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<ConfiguracaoFormulario> buscarPorNomeAcao(String nomeAcao);
    
    /**
     * Busca uma configuração de formulário pelo ID da ação.
     * @param acaoId O ID da ação.
     * @return Um Optional contendo o ConfiguracaoFormulario se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<ConfiguracaoFormulario> buscarPorAcaoId(Long acaoId);

    /**
     * Deleta uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return true se a configuração foi deletada, false caso contrário.
     */
    boolean deletarPorNomeAcao(String nomeAcao);
    
    /**
     * Deleta uma configuração de formulário pelo ID da ação.
     * @param acaoId O ID da ação.
     * @return true se a configuração foi deletada, false caso contrário.
     */
    boolean deletarPorAcaoId(Long acaoId);

    /**
     * Verifica se existe uma configuração para a ação especificada.
     * @param nomeAcao O nome da ação.
     * @return true se existir, false caso contrário.
     */
    boolean existePorNomeAcao(String nomeAcao);
    
    /**
     * Verifica se existe uma configuração para a ação especificada pelo ID.
     * @param acaoId O ID da ação.
     * @return true se existir, false caso contrário.
     */
    boolean existePorAcaoId(Long acaoId);
}