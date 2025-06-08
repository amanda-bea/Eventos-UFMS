package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;

import java.util.HashSet;
import java.util.List;

/**
 * Interface para padronizar as operações de repositório para a entidade Acao.
 * Esta interface será implementada tanto por AcaoRepository (implementação em memória)
 * quanto por AcaoRepositoryJDBC (implementação com persistência em banco de dados).
 */
public interface IAcaoRepository {
    
    /**
     * Retorna todas as ações armazenadas.
     * @return Um conjunto de todas as ações.
     */
    HashSet<Acao> getAcoes();
    
    /**
     * Adiciona uma nova ação.
     * @param acao A ação a ser adicionada.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addAcao(Acao acao);
    
    /**
     * Remove uma ação.
     * @param acao A ação a ser removida.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removeAcao(Acao acao);
    
    /**
     * Atualiza uma ação existente.
     * @param acao A ação com as informações atualizadas.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean updateAcao(Acao acao);
    
    /**
     * Busca uma ação pelo nome.
     * @param nome O nome da ação a ser buscada.
     * @return A ação encontrada, ou null se não encontrada.
     */
    Acao getAcao(String nome);
    
    /**
     * Remove uma ação pelo nome.
     * @param nome O nome da ação a ser removida.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean deleteAcao(String nome);
    
    /**
     * Busca uma ação pelo ID.
     * @param id O ID da ação a ser buscada.
     * @return A ação encontrada, ou null se não encontrada.
     */
    Acao findById(Long id);
    
    /**
     * Busca ações associadas a um evento específico.
     * @param eventoId O ID do evento.
     * @return Uma lista de ações associadas ao evento.
     */
    List<Acao> findByEventoId(Long eventoId);
    
    /**
     * Remove uma ação pelo ID.
     * @param id O ID da ação a ser removida.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean delete(Long id);
}
