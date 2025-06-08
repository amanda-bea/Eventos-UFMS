package com.ufms.eventos.repository;

import java.util.Optional;

import com.ufms.eventos.model.ConfiguracaoFormulario;

/**
 * Interface para padronizar as operações de repositório para a entidade ConfiguracaoFormulario.
 * Esta interface será implementada tanto por ConfiguracaoFormularioRepository (implementação em memória)
 * quanto por ConfiguracaoFormularioRepositoryJDBC (implementação com persistência em banco de dados).
 */
public interface IConfiguracaoFormularioRepository {

    /**
     * Salva ou atualiza uma configuração de formulário.
     * @param configuracao A configuração a ser salva.
     * @return A configuração salva.
     */
    ConfiguracaoFormulario salvar(ConfiguracaoFormulario configuracao);

    /**
     * Busca uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return Um Optional contendo a configuração, se encontrada.
     */
    Optional<ConfiguracaoFormulario> buscarPorNomeAcao(String nomeAcao);

    /**
     * Remove uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean deletarPorNomeAcao(String nomeAcao);
    
    /**
     * Verifica se existe uma configuração de formulário para a ação especificada.
     * @param nomeAcao O nome da ação.
     * @return true se existir, false caso contrário.
     */
    boolean existePorNomeAcao(String nomeAcao);
}
