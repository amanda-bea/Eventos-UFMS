package com.ufms.eventos.repository;

import com.ufms.eventos.model.Usuario;
import java.util.Set;

/**
 * Interface para repositório de usuários.
 */
public interface UsuarioRepository {
    
    /**
     * Retorna todos os usuários.
     * @return Um conjunto com todos os usuários.
     */
    Set<Usuario> getUsuarios();
    
    /**
     * Busca um usuário pelo nome.
     * @param nome O nome do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    Usuario getUsuarioPorNome(String nome);
    
    /**
     * Busca um usuário pelo nome e senha.
     * @param nome O nome do usuário.
     * @param senha A senha do usuário.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    Usuario getUsuarioPorNomeESenha(String nome, String senha);
    
    /**
     * Adiciona um novo usuário.
     * @param usuario O usuário a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addUsuario(Usuario usuario);
    
    /**
     * Atualiza os dados de um usuário existente.
     * @param usuario O usuário com as informações atualizadas.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean updateUsuario(Usuario usuario);
    
    /**
     * Remove um usuário.
     * @param usuario O usuário a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removeUsuario(Usuario usuario);
}