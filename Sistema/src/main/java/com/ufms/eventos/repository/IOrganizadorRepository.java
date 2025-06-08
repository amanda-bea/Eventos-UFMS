package com.ufms.eventos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import com.ufms.eventos.model.Organizador;

/**
 * Interface para padronizar as operações de repositório para a entidade Organizador.
 * Esta interface será implementada tanto por OrganizadorRepository (implementação em memória)
 * quanto por OrganizadorRepositoryJDBC (implementação com persistência em banco de dados).
 */
public interface IOrganizadorRepository {
    
    /**
     * Retorna todos os organizadores armazenados.
     * @return Um conjunto de todos os organizadores.
     */
    public HashSet<Organizador> getOrganizadores();

    /**
     * Adiciona um novo organizador.
     * @param organizador O organizador a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean addOrganizador(Organizador organizador);

    /**
     * Remove um organizador.
     * @param organizador O organizador a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean removeOrganizador(Organizador organizador);

    /**
     * Busca um organizador pelo nome.
     * @param nome O nome do organizador a ser buscado.
     * @return O organizador encontrado, ou null se não encontrado.
     */
    public Organizador getOrganizador(String nome);

    /**
     * Salva ou atualiza um organizador.
     * @param organizador O organizador a ser salvo.
     * @return O organizador salvo.
     */
    public Organizador salvar(Organizador organizador);

    /**
     * Busca um organizador pelo ID.
     * @param id O ID do organizador a ser buscado.
     * @return O organizador encontrado, ou null se não encontrado.
     */
    public Organizador findById(Long id);

    /**
     * Converte um ResultSet para um objeto Organizador.
     * Este método é específico para implementações que usam JDBC.
     * @param rs O ResultSet contendo os dados do organizador.
     * @return O organizador construído.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    Organizador mapResultSetToOrganizador(ResultSet rs) throws SQLException;
}
