package com.ufms.eventos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import com.ufms.eventos.model.Usuario;

/**
 * Interface para padronizar as operações de repositório para a entidade Usuario.
 * Esta interface será implementada tanto por UsuarioRepository (implementação em memória)
 * quanto por UsuarioRepositoryJDBC (implementação com persistência em banco de dados).
 */
public interface IUsuarioRepository {

    /**
     * Adiciona um novo usuário.
     * @param usuario O usuário a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean addUsuario(Usuario usuario);

    /**
     * Remove um usuário.
     * @param usuario O usuário a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean removeUsuario(Usuario usuario);

    /**
     * Atualiza um usuário existente.
     * @param usuario O usuário com as informações atualizadas.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean updateUsuario(Usuario usuario);

    /**
     * Busca um usuário pelo nome.
     * @param nome O nome do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    public Usuario getUsuarioPorNome(String nome);

    /**
     * Busca um usuário pelo nome e senha.
     * @param nome O nome do usuário.
     * @param senha A senha do usuário.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    public Usuario getUsuarioPorNomeESenha(String nome, String senha);

    /**
     * Retorna todos os usuários armazenados.
     * @return Um conjunto de todos os usuários.
     */
    public Set<Usuario> getUsuarios();

    /**
     * Constrói um objeto Usuario a partir de um ResultSet.
     * Este método é específico para implementações que usam JDBC.
     * @param rs O ResultSet contendo os dados do usuário.
     * @return O usuário construído.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    Usuario construirUsuario(ResultSet rs) throws SQLException;
}
