package com.ufms.eventos.repository;

import com.ufms.eventos.model.Usuario;
import java.util.HashSet;
import java.util.Iterator;

public class UsuarioRepository {
    private HashSet<Usuario> usuarios;

    public UsuarioRepository() {
        this.usuarios = new HashSet<Usuario>();
    }

    public HashSet<Usuario> getUsuarios() {
        return new HashSet<Usuario>(this.usuarios); //retorna uma cópia do conjunto de eventos
    }

    public boolean addUsuario(Usuario usuario) {
        return this.usuarios.add(usuario);
    }

    public boolean removeUsuario(Usuario usuario) {
        return this.usuarios.remove(usuario);
    }

    public boolean updateUsuario(Usuario usuario) { // não cho que vai preicsar mas deixei por precauão
        if (this.usuarios.contains(usuario)) {
            this.usuarios.remove(usuario);
            this.usuarios.add(usuario);
            return true;
        }
        return false;
    }

    public Usuario getUsuario(String nome) {
        Iterator<Usuario> iterator = this.usuarios.iterator();

        Usuario usuario = null;
        while (iterator.hasNext()) {
            Usuario u = iterator.next();
            if (u.getNome().equals(nome)) {
                usuario = u;
                break;
            }
        }
        return usuario;
    }

    public Usuario getUsuarioPorNomeESenha(String nome, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equalsIgnoreCase(nome) && usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario getUsuarioPorNome(String nome) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equalsIgnoreCase(nome)) {
                return usuario;
            }
        }
        return null;
    }

}
