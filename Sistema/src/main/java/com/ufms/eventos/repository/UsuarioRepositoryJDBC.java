package com.ufms.eventos.repository;

import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementação de IUsuarioRepository que utiliza JDBC para persistência em banco de dados.
 */
public class UsuarioRepositoryJDBC implements IUsuarioRepository {

    /**
     * Retorna todos os usuários do banco de dados.
     * @return Um conjunto com todos os usuários.
     */
    @Override
    public Set<Usuario> getUsuarios() {
        Set<Usuario> usuarios = new HashSet<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(construirUsuario(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    /**
     * Busca um usuário pelo nome.
     * @param nome O nome do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    @Override
    public Usuario getUsuarioPorNome(String nome) {
        String sql = "SELECT * FROM usuarios WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return construirUsuario(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca um usuário pelo nome e senha.
     * @param nome O nome do usuário.
     * @param senha A senha do usuário.
     * @return O usuário encontrado, ou null se não encontrado.
     */
    @Override
    public Usuario getUsuarioPorNomeESenha(String nome, String senha) {
        String sql = "SELECT * FROM usuarios WHERE nome = ? AND senha = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, senha);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return construirUsuario(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Constrói um objeto Usuario a partir de um ResultSet.
     * @param rs O ResultSet contendo os dados do usuário.
     * @return O usuário construído.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    @Override
    public Usuario construirUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setTelefone(rs.getString("telefone"));
        return usuario;
    }

    /**
     * Adiciona um novo usuário ao banco de dados.
     * @param usuario O usuário a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    @Override
    public boolean addUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, telefone) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getSenha());
            pstmt.setString(4, usuario.getTelefone());

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove um usuário do banco de dados.
     * @param usuario O usuário a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    @Override
    public boolean removeUsuario(Usuario usuario) {
        return deleteByNome(usuario.getNome());
    }

    /**
     * Remove um usuário do banco de dados pelo nome.
     * @param nome O nome do usuário a ser removido.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    private boolean deleteByNome(String nome) {
        String sql = "DELETE FROM usuarios WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Atualiza os dados de um usuário existente.
     * @param usuario O usuário com as informações atualizadas.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    @Override
    public boolean updateUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET email = ?, senha = ?, telefone = ? WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getEmail());
            pstmt.setString(2, usuario.getSenha());
            pstmt.setString(3, usuario.getTelefone());
            pstmt.setString(4, usuario.getNome());

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
