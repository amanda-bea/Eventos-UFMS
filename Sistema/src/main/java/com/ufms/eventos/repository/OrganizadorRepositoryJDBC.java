package com.ufms.eventos.repository;

import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.HashSet;

/**
 * Implementação JDBC do repositório de organizadores.
 */
public class OrganizadorRepositoryJDBC implements OrganizadorRepository {

    @Override
    public HashSet<Organizador> getOrganizadores() {
        HashSet<Organizador> organizadores = new HashSet<>();
        String sql = "SELECT o.nome, u.email, u.telefone " +
                     "FROM organizadores o " +
                     "JOIN usuarios u ON o.nome = u.nome";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                organizadores.add(mapResultSetToOrganizador(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return organizadores;
    }

    @Override
    public boolean addOrganizador(Organizador organizador) {
        // 1. Verificar se usuário existe
        String checkSql = "SELECT 1 FROM usuarios WHERE nome = ?";
        // 2. Se necessário, inserir usuário
        String insertUsuarioSql = "INSERT INTO usuarios (nome, email, senha, telefone) VALUES (?, ?, ?, ?)";
        // 3. Inserir organizador
        String insertOrgSql = "INSERT INTO organizadores (nome) VALUES (?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertUsuarioStmt = conn.prepareStatement(insertUsuarioSql);
             PreparedStatement insertOrgStmt = conn.prepareStatement(insertOrgSql)) {

            String nome = organizador.getNome();
            
            // Verifica se usuário existe
            checkStmt.setString(1, nome);
            ResultSet rs = checkStmt.executeQuery();
            
            // Se não existir, insere usuário primeiro
            if (!rs.next()) {
                insertUsuarioStmt.setString(1, nome);
                insertUsuarioStmt.setString(2, organizador.getEmail());
                insertUsuarioStmt.setString(3, organizador.getSenha());
                insertUsuarioStmt.setString(4, organizador.getTelefone());
                insertUsuarioStmt.executeUpdate();
            }
            
            // Insere organizador
            insertOrgStmt.setString(1, nome);
            return insertOrgStmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean removeOrganizador(Organizador organizador) {
        String sql = "DELETE FROM organizadores WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, organizador.getNome());
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Organizador getOrganizador(String nome) {
        String sql = "SELECT o.nome, u.email, u.telefone, u.senha " +
                     "FROM organizadores o " +
                     "JOIN usuarios u ON o.nome = u.nome " +
                     "WHERE LOWER(o.nome) = LOWER(?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrganizador(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Organizador salvar(Organizador organizador) {
        if (organizador == null || organizador.getNome() == null || organizador.getNome().trim().isEmpty()) {
            return null;
        }

        Organizador existente = getOrganizador(organizador.getNome());

        if (existente != null) {
            // Atualiza apenas os dados do usuário, pois o organizador não tem atributos próprios além do nome
            String sql = "UPDATE usuarios SET email = ?, telefone = ? WHERE nome = ?";

            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, organizador.getEmail());
                stmt.setString(2, organizador.getTelefone());
                stmt.setString(3, organizador.getNome());

                if (stmt.executeUpdate() > 0) {
                    return organizador;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // Insere novo
            if (addOrganizador(organizador)) {
                return organizador;
            }
        }

        return null;
    }

    @Override
    public boolean verificarOrganizador(String nome) {
        String sql = "SELECT 1 FROM organizadores WHERE LOWER(nome) = LOWER(?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cria um objeto Organizador a partir do resultado da consulta.
     */
    private Organizador mapResultSetToOrganizador(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String telefone = rs.getString("telefone");
        
        // Tenta obter senha, mas pode não estar disponível em todas as consultas
        String senha = null;
        try {
            senha = rs.getString("senha");
        } catch (SQLException e) {
            // Ignora erro, senha pode não estar presente em algumas consultas
        }
        
        // Cria o usuário base
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        if (senha != null) {
            usuario.setSenha(senha);
        }
        
        // Cria o organizador
        Organizador organizador = new Organizador();
        organizador.setNome(nome);
        organizador.setEmail(email);
        organizador.setTelefone(telefone);
        if (senha != null) {
            organizador.setSenha(senha);
        }
        return organizador;
    }
}