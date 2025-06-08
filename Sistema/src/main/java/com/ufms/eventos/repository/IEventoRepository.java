package com.ufms.eventos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;

/**
 * Interface para padronizar as operações de repositório para a entidade Evento.
 * Esta interface será implementada tanto por EventoRepository (implementação em memória)
 * quanto por EventoRepositoryJDBC (implementação com persistência em banco de dados).
 */
public interface IEventoRepository {
    
    /**
     * Adiciona um novo evento.
     * @param evento O evento a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addEvento(Evento evento);

    /**
     * Remove um evento.
     * @param evento O evento a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removeEvento(Evento evento);

    /**
     * Atualiza um evento existente.
     * @param evento O evento com as informações atualizadas.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean updateEvento(Evento evento);

    /**
     * Busca um evento pelo nome.
     * @param nome O nome do evento a ser buscado.
     * @return O evento encontrado, ou null se não encontrado.
     */
    Evento getEvento(String nome);

    /**
     * Busca um evento pelo ID.
     * @param id O ID do evento a ser buscado.
     * @return O evento encontrado, ou null se não encontrado.
     */
    Evento findById(Long id);

    /**
     * Retorna todos os eventos armazenados.
     * @return Um conjunto de todos os eventos.
     */
    HashSet<Evento> getEventos();

    /**
     * Retorna todos os eventos com status "Aguardando aprovação".
     * @return Um conjunto de todos os eventos que são solicitações.
     */
    HashSet<Evento> getSolicitacoes();

    /**
     * Constrói um objeto Evento a partir de um ResultSet.
     * Este método é específico para implementações que usam JDBC.
     * @param rs O ResultSet contendo os dados do evento.
     * @return O evento construído.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    Evento construirEvento(ResultSet rs) throws SQLException;
}
