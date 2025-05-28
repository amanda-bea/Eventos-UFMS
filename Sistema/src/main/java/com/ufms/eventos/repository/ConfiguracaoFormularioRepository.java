package com.ufms.eventos.repository;

import com.ufms.eventos.model.ConfiguracaoFormulario;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional; // Usar Optional para buscas pode ser uma boa prática

public class ConfiguracaoFormularioRepository {

    // Armazenamento em memória. Em um sistema real, isso seria um banco de dados.
    private final Map<String, ConfiguracaoFormulario> db = new HashMap<>();

    /**
     * Salva ou atualiza uma configuração de formulário.
     * @param configuracao O objeto ConfiguracaoFormulario a ser salvo.
     * @return O objeto ConfiguracaoFormulario salvo.
     */
    public ConfiguracaoFormulario salvar(ConfiguracaoFormulario configuracao) {
        if (configuracao == null || configuracao.getNomeAcao() == null || configuracao.getNomeAcao().trim().isEmpty()) {
            // Em uma aplicação real, poderia lançar uma exceção aqui.
            System.err.println("ConfiguracaoFormularioRepository: Tentativa de salvar configuração nula ou sem nome de ação.");
            return null;
        }
        db.put(configuracao.getNomeAcao(), configuracao);
        return configuracao;
    }

    /**
     * Busca uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return Um Optional contendo o ConfiguracaoFormulario se encontrado, ou Optional.empty() caso contrário.
     */
    public Optional<ConfiguracaoFormulario> buscarPorNomeAcao(String nomeAcao) {
        if (nomeAcao == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(db.get(nomeAcao));
    }

    /**
     * Deleta uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return true se a configuração foi deletada, false caso contrário.
     */
    public boolean deletarPorNomeAcao(String nomeAcao) {
        if (nomeAcao == null) {
            return false;
        }
        return db.remove(nomeAcao) != null;
    }

    /**
     * Verifica se existe uma configuração para a ação especificada.
     * @param nomeAcao O nome da ação.
     * @return true se existir, false caso contrário.
     */
    public boolean existePorNomeAcao(String nomeAcao) {
        if (nomeAcao == null) {
            return false;
        }
        return db.containsKey(nomeAcao);
    }
}