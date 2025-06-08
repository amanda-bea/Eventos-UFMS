package com.ufms.eventos.repository;

import com.ufms.eventos.model.Usuario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UsuarioRepository implements IUsuarioRepository {
    private HashSet<Usuario> usuarios;

    public UsuarioRepository() {
        this.usuarios = new HashSet<Usuario>();
    }

    @Override
    public Set<Usuario> getUsuarios() {
        return new HashSet<Usuario>(this.usuarios); //retorna uma cópia do conjunto de eventos
    }

    @Override
    public boolean addUsuario(Usuario usuario) {
        return this.usuarios.add(usuario);
    }

    @Override
    public boolean removeUsuario(Usuario usuario) {
        return this.usuarios.remove(usuario);
    }

    @Override
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

    @Override
    public Usuario getUsuarioPorNomeESenha(String nome, String senha) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equalsIgnoreCase(nome) && usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public Usuario getUsuarioPorNome(String nome) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().equalsIgnoreCase(nome)) {
                return usuario;
            }
        }
        return null;
    }

    /**
     * Método não utilizado na implementação em memória, mas necessário para cumprir o contrato da interface.
     */
    @Override
    public Usuario construirUsuario(ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("Método não suportado pela implementação em memória");
    }
}
