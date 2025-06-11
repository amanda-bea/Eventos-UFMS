package com.ufms.eventos.services;

import com.ufms.eventos.repository.UsuarioRepository;
import com.ufms.eventos.repository.UsuarioRepositoryJDBC;
import com.ufms.eventos.model.Usuario;

/**
 * Camada de Serviço que contém a lógica de negócio para Usuários.
 * Esta classe segue o mesmo padrão da classe AcaoService.
 */
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepositoryJDBC();
    }
    

    public Usuario autenticar(String nome, String senha) {
        if (nome == null || senha == null || nome.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.getUsuarioPorNomeESenha(nome, senha);
    }

    public Usuario buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.getUsuarioPorNome(nome);
    }

    public boolean cadastrarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            System.err.println("Erro ao cadastrar usuário: dados incompletos");
            return false;
        }
        
        // Verifica se já existe um usuário com o mesmo nome
        if (usuarioRepository.getUsuarioPorNome(usuario.getNome()) != null) {
            System.err.println("Erro ao cadastrar usuário: nome de usuário '" + usuario.getNome() + "' já existe");
            return false; // Usuário já existe
        }
        
        boolean sucesso = usuarioRepository.addUsuario(usuario);
        if (sucesso) {
            System.out.println("Usuário '" + usuario.getNome() + "' cadastrado com sucesso");
        } else {
            System.err.println("Erro ao cadastrar usuário no banco de dados");
        }
        
        return sucesso;
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param usuario O usuário com as informações atualizadas.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            System.err.println("Erro ao atualizar usuário: dados incompletos");
            return false;
        }
        
        // Verifica se o usuário existe
        if (usuarioRepository.getUsuarioPorNome(usuario.getNome()) == null) {
            System.err.println("Erro ao atualizar usuário: usuário '" + usuario.getNome() + "' não encontrado");
            return false;
        }
        
        boolean sucesso = usuarioRepository.updateUsuario(usuario);
        if (sucesso) {
            System.out.println("Usuário '" + usuario.getNome() + "' atualizado com sucesso");
        } else {
            System.err.println("Erro ao atualizar usuário no banco de dados");
        }
        
        return sucesso;
    }

}