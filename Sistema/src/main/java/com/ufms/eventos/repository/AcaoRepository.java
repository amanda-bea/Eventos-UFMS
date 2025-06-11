package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import java.util.HashSet;
import java.util.List;

public interface AcaoRepository {
    
    /**
     * Retorna todas as ações armazenadas no banco de dados.
     * @return Um conjunto de todas as ações.
     */
    HashSet<Acao> getAcoes();
    
    /**
     * Adiciona uma nova ação ao banco de dados.
     * @param acao A ação a ser adicionada.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addAcao(Acao acao);
    
    /**
     * Remove uma ação do banco de dados.
     * @param acao A ação a ser removida.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removeAcao(Acao acao);
    
    /**
     * Atualiza uma ação existente no banco de dados.
     * @param acao A ação com os dados atualizados.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean updateAcao(Acao acao);
    
    /**
     * Busca uma ação pelo seu nome no banco de dados.
     * @param nome O nome da ação a ser buscada.
     * @return A ação correspondente ou null se não encontrada.
     */
    Acao getAcao(String nome);
    
    /**
     * Remove uma ação pelo seu nome do banco de dados.
     * @param nome O nome da ação a ser removida.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean deleteAcao(String nome);
    
    /**
     * Busca uma ação pelo seu ID no banco de dados.
     * @param id O ID da ação a ser buscada.
     * @return A ação correspondente ou null se não encontrada.
     */
    Acao findById(Long id);
    
    /**
     * Busca todas as ações associadas a um evento específico no banco de dados.
     * @param eventoId O ID do evento.
     * @return Lista de ações associadas ao evento.
     */
    List<Acao> findByEventoId(Long eventoId);
}