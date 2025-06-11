package com.ufms.eventos.repository;

import com.ufms.eventos.model.Admin;
import java.util.HashSet;

public interface AdminRepository {
    
    /**
     * Retorna todos os administradores cadastrados no banco de dados.
     * @return Um conjunto com todos os administradores.
     */
    HashSet<Admin> getAdmins();
    
    /**
     * Adiciona um novo administrador ao banco de dados.
     * @param admin O administrador a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addAdmin(Admin admin);
    
    /**
     * Remove um administrador do banco de dados usando o nome.
     * @param nome O nome do administrador a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removeAdmin(String nome);
    
    /**
     * Busca um administrador pelo nome.
     * @param nome O nome do administrador.
     * @return O administrador encontrado ou null se não existir.
     */
    Admin findByNome(String nome);
    
    /**
     * Verifica se um usuário com o nome fornecido é um administrador.
     * @param nome O nome do usuário a verificar.
     * @return true se o usuário é administrador, false caso contrário.
     */
    boolean isAdmin(String nome);
}