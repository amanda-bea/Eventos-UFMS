package com.ufms.eventos.services;

import com.ufms.eventos.repository.IUsuarioRepository;
import com.ufms.eventos.repository.UsuarioRepositoryJDBC;
import com.ufms.eventos.model.Usuario;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Camada de Serviço que contém a lógica de negócio para Usuários.
 * Esta classe segue o mesmo padrão da classe AcaoService.
 */
public class UsuarioService {
    private IUsuarioRepository usuarioRepository;

    /**
     * Construtor que inicializa o repositório de usuários.
     */
    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepositoryJDBC();
    }

    /**
     * Autentica um usuário pelo nome e senha.
     * @param nome O nome do usuário.
     * @param senha A senha do usuário.
     * @return O usuário autenticado, ou null se a autenticação falhar.
     */
    public Usuario autenticar(String nome, String senha) {
        return usuarioRepository.getUsuarioPorNomeESenha(nome, senha);
    }

    /**
     * Busca um usuário pelo nome.
     * @param nome O nome do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    public Usuario buscarPorNome(String nome) {
        return usuarioRepository.getUsuarioPorNome(nome);
    }

    /**
     * Cadastra um novo usuário no sistema.
     * @param usuario O usuário a ser cadastrado.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean cadastrarUsuario(Usuario usuario) {
        // Verifica se já existe um usuário com o mesmo nome
        if (usuarioRepository.getUsuarioPorNome(usuario.getNome()) != null) {
            return false; // Usuário já existe
        }
        
        return usuarioRepository.addUsuario(usuario);
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param usuario O usuário com as informações atualizadas.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizarUsuario(Usuario usuario) {
        // Verifica se o usuário existe
        if (usuarioRepository.getUsuarioPorNome(usuario.getNome()) == null) {
            return false; // Usuário não existe
        }
        
        return usuarioRepository.updateUsuario(usuario);
    }

    /**
     * Remove um usuário do sistema.
     * @param usuario O usuário a ser removido.
     * @return true se a remoção foi bem-sucedida, false caso contrário.
     */
    public boolean removerUsuario(Usuario usuario) {
        return usuarioRepository.removeUsuario(usuario);
    }

    /**
     * Remove um usuário do sistema pelo nome.
     * @param nome O nome do usuário a ser removido.
     * @return true se a remoção foi bem-sucedida, false caso contrário.
     */
    public boolean removerUsuarioPorNome(String nome) {
        Usuario usuario = usuarioRepository.getUsuarioPorNome(nome);
        if (usuario != null) {
            return usuarioRepository.removeUsuario(usuario);
        }
        return false;
    }

    /**
     * Lista todos os usuários cadastrados.
     * @return Um conjunto com todos os usuários.
     */
    public Set<Usuario> listarTodosUsuarios() {
        return usuarioRepository.getUsuarios();
    }
}
