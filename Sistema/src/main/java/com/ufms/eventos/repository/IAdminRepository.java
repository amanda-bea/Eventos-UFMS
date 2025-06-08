package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Admin;

import java.util.HashSet;
import java.util.List;

public interface IAdminRepository {

    /**
     * Retorna todas as ações armazenadas.
     * @return Um conjunto de todas as ações
    */
    HashSet<Admin> getAdmins();

    /**
     * Adiciona um novo administrador.
     * @param admin O admin a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean addAdmin(Admin admin);
    
    /**
     * Remove um administrador.
     * @param admin O admin a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    boolean removeAdmin(Admin admin);
}
