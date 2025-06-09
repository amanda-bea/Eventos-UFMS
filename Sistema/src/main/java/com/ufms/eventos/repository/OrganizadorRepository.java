package com.ufms.eventos.repository;

import com.ufms.eventos.model.Organizador;
import java.util.HashSet;

/**
 * Interface que define as operações do repositório de organizadores.
 */
public interface OrganizadorRepository {
    
    /**
     * Retorna todos os organizadores cadastrados.
     * @return Um conjunto de organizadores.
     */
    HashSet<Organizador> getOrganizadores();
    
    /**
     * Adiciona um novo organizador.
     * @param organizador O organizador a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addOrganizador(Organizador organizador);
    
    /**
     * Remove um organizador.
     * @param organizador O organizador a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removeOrganizador(Organizador organizador);
    
    /**
     * Busca um organizador pelo nome.
     * @param nome O nome do organizador.
     * @return O organizador encontrado ou null se não existir.
     */
    Organizador getOrganizador(String nome);
    
    /**
     * Salva ou atualiza um organizador.
     * @param organizador O organizador a ser salvo.
     * @return O organizador salvo ou null em caso de erro.
     */
    Organizador salvar(Organizador organizador);
    
    /**
     * Verifica se existe um organizador com o nome especificado.
     * @param nome Nome do organizador.
     * @return true se existir, false caso contrário.
     */
    boolean verificarOrganizador(String nome);
}