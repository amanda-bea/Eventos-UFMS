package com.ufms.eventos.repository;

import com.ufms.eventos.model.PresencaConfirmada;
import java.util.HashSet;

/**
 * Interface para repositório de presenças confirmadas.
 */
public interface PresencaConfirmadaRepository {
    
    /**
     * Adiciona uma presença confirmada.
     * @param presenca A presença a ser adicionada.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addPresencaConfirmada(PresencaConfirmada presenca);
    
    /**
     * Remove uma presença confirmada.
     * @param presenca A presença a ser removida.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removePresencaConfirmada(PresencaConfirmada presenca);
    
    /**
     * Retorna todas as presenças confirmadas.
     * @return Um conjunto de presenças confirmadas.
     */
    HashSet<PresencaConfirmada> getPresencasConfirmadas();
    
    /**
     * Busca presenças confirmadas por nome de usuário.
     * @param nomeUsuario O nome do usuário.
     * @return Um conjunto de presenças confirmadas do usuário.
     */
    HashSet<PresencaConfirmada> getPresencasPorUsuario(String nomeUsuario);
    
    /**
     * Busca presenças confirmadas por ID da ação.
     * @param acaoId O ID da ação.
     * @return Um conjunto de presenças confirmadas para a ação.
     */
    HashSet<PresencaConfirmada> getPresencasPorAcao(Long acaoId);
    
    /**
     * Verifica se um usuário tem presença confirmada em uma ação.
     * @param nomeUsuario O nome do usuário.
     * @param acaoId O ID da ação.
     * @return true se o usuário tem presença confirmada, false caso contrário.
     */
    boolean verificarPresenca(String nomeUsuario, Long acaoId);
    
    /**
     * Conta o número de presenças confirmadas para uma ação.
     * @param acaoId O ID da ação.
     * @return O número de presenças confirmadas.
     */
    int contarPresencasPorAcao(Long acaoId);
}