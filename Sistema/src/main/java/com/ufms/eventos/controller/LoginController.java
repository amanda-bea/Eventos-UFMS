package com.ufms.eventos.controller;

import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.UsuarioService;

public class LoginController {
    private UsuarioService usuarioService;
    private Usuario usuarioLogado;

    public LoginController() {
        this.usuarioService = new UsuarioService();
        this.usuarioLogado = null;
    }

    // Método para autenticar usuário
    public boolean autenticar(String nome, String senha) {
        Usuario usuario = usuarioService.autenticar(nome, senha);
        if (usuario != null) {
            this.usuarioLogado = usuario;
            return true;
        }
        return false;
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    public void logout() {
        this.usuarioLogado = null;
    }
}