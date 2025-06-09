package com.ufms.eventos.services;

import com.ufms.eventos.repository.UsuarioRepository;
import com.ufms.eventos.repository.UsuarioRepositoryJDBC;
import com.ufms.eventos.model.Usuario;

import java.util.HashSet;
import java.util.Set;

/**
 * Camada de Serviço que contém a lógica de negócio para Usuários.
 * Esta classe segue o mesmo padrão da classe AcaoService.
 */
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    /**
     * Construtor que inicializa o repositório de usuários.
     */
    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepositoryJDBC();
    }
    
    /**
     * Construtor para injeção de dependência (facilita testes).
     * @param usuarioRepository O repositório de usuários a ser utilizado.
     */
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica um usuário pelo nome e senha.
     * @param nome O nome do usuário.
     * @param senha A senha do usuário.
     * @return O usuário autenticado, ou null se a autenticação falhar.
     */
    public Usuario autenticar(String nome, String senha) {
        if (nome == null || senha == null || nome.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.getUsuarioPorNomeESenha(nome, senha);
    }

    /**
     * Busca um usuário pelo nome.
     * @param nome O nome do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    public Usuario buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.getUsuarioPorNome(nome);
    }

    /**
     * Cadastra um novo usuário no sistema.
     * @param usuario O usuário a ser cadastrado.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
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
            return false; // Usuário não existe
        }
        
        boolean sucesso = usuarioRepository.updateUsuario(usuario);
        if (sucesso) {
            System.out.println("Usuário '" + usuario.getNome() + "' atualizado com sucesso");
        } else {
            System.err.println("Erro ao atualizar usuário no banco de dados");
        }
        
        return sucesso;
    }

    /**
     * Remove um usuário do sistema.
     * @param usuario O usuário a ser removido.
     * @return true se a remoção foi bem-sucedida, false caso contrário.
     */
    public boolean removerUsuario(Usuario usuario) {
        if (usuario == null || usuario.getNome() == null) {
            System.err.println("Erro ao remover usuário: dados incompletos");
            return false;
        }
        
        boolean sucesso = usuarioRepository.removeUsuario(usuario);
        if (sucesso) {
            System.out.println("Usuário '" + usuario.getNome() + "' removido com sucesso");
        } else {
            System.err.println("Erro ao remover usuário: usuário não encontrado ou erro no banco de dados");
        }
        
        return sucesso;
    }

    /**
     * Remove um usuário do sistema pelo nome.
     * @param nome O nome do usuário a ser removido.
     * @return true se a remoção foi bem-sucedida, false caso contrário.
     */
    public boolean removerUsuarioPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            System.err.println("Erro ao remover usuário: nome não fornecido");
            return false;
        }
        
        Usuario usuario = usuarioRepository.getUsuarioPorNome(nome);
        if (usuario == null) {
            System.err.println("Erro ao remover usuário: usuário '" + nome + "' não encontrado");
            return false;
        }
        
        boolean sucesso = usuarioRepository.removeUsuario(usuario);
        if (sucesso) {
            System.out.println("Usuário '" + nome + "' removido com sucesso");
        } else {
            System.err.println("Erro ao remover usuário do banco de dados");
        }
        
        return sucesso;
    }

    /**
     * Lista todos os usuários cadastrados.
     * @return Um conjunto com todos os usuários.
     */
    public Set<Usuario> listarTodosUsuarios() {
        Set<Usuario> usuarios = usuarioRepository.getUsuarios();
        if (usuarios == null) {
            return new HashSet<>(); // Retorna um conjunto vazio se o resultado for null
        }
        return usuarios;
    }
    
    /**
     * Verifica se um usuário existe pelo nome.
     * @param nome O nome do usuário a verificar.
     * @return true se o usuário existe, false caso contrário.
     */
    public boolean existeUsuario(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.getUsuarioPorNome(nome) != null;
    }
}