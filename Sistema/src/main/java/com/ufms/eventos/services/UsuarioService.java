package com.ufms.eventos.services;

import com.ufms.eventos.repository.UsuarioRepository;
import com.ufms.eventos.model.Usuario;

public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuario autenticar(String nome, String senha) {
        return usuarioRepository.getUsuarioPorNomeESenha(nome, senha);
    }

    public Usuario buscarPorNome(String nome) {
        return usuarioRepository.getUsuarioPorNome(nome);
    }

}