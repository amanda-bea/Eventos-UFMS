package com.ufms.eventos.util;

import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;

/**
 * Gerencia a sessão do usuário em toda a aplicação usando o padrão Singleton.
 * Garante que apenas uma instância desta classe exista, mantendo um estado de
 * login global e acessível de qualquer controller.
 */
public final class SessaoUsuario {

    private static SessaoUsuario instancia;
    private Usuario usuarioLogado;

    // Construtor privado para impedir a criação de novas instâncias.
    private SessaoUsuario() {}

    /**
     * Retorna a única instância da sessão. Se não existir, cria uma.
     * @return A instância única de SessaoUsuario.
     */
    public static SessaoUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SessaoUsuario();
        }
        return instancia;
    }

    /**
     * Inicia a sessão para um usuário.
     * @param usuario O usuário que fez o login (pode ser Admin ou Usuario).
     */
    public void login(Usuario usuario) {
        this.usuarioLogado = usuario;
        System.out.println("Sessão iniciada para: " + usuario.getNome() + " | Tipo: " + usuario.getClass().getSimpleName());
    }

    /**
     * Finaliza a sessão atual, deslogando o usuário.
     */
    public void logout() {
        this.usuarioLogado = null;
        System.out.println("Sessão finalizada.");
    }

    /**
     * Retorna o usuário atualmente logado.
     * @return O objeto Usuario da sessão, ou null se ninguém estiver logado.
     */
    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    /**
     * Método utilitário para verificar se o usuário logado é um Admin.
     * @return true se o usuário é uma instância de Admin, false caso contrário.
     */
    public boolean isAdmin() {
        return this.usuarioLogado instanceof Admin;
    }
}