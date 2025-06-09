package com.ufms.eventos.repository;

import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.HashSet;

/**
 * Implementação JDBC do repositório de administradores.
 */
public class AdminRepositoryJDBC implements AdminRepository {
    
    @Override
    public HashSet<Admin> getAdmins() {
        HashSet<Admin> admins = new HashSet<>();
        String sql = "SELECT a.nome, a.cargo, u.email, u.telefone " +
                     "FROM admins a " +
                     "JOIN usuarios u ON a.nome = u.nome";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String cargo = rs.getString("cargo");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                
                // Cria o usuário e o admin a partir dos dados
                Usuario usuario = new Usuario();
                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setTelefone(telefone);
                
                Admin admin = new Admin();
                admin.setNome(nome);
                admin.setEmail(email);
                admin.setTelefone(telefone);
                admin.setCargo(cargo);
                
                admins.add(admin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return admins;
    }

    @Override
    public boolean addAdmin(Admin admin) {
        // Verifica se o usuário já existe
        String checkSql = "SELECT nome FROM usuarios WHERE nome = ?";
        String insertUsuarioSql = "INSERT INTO usuarios (nome, email, senha, telefone) VALUES (?, ?, ?, ?)";
        String insertAdminSql = "INSERT INTO admins (nome, cargo) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertUsuarioStmt = conn.prepareStatement(insertUsuarioSql);
             PreparedStatement insertAdminStmt = conn.prepareStatement(insertAdminSql)) {
            
            // 1. Verifica se o usuário já existe
            String nome = admin.getNome();
            checkStmt.setString(1, nome);
            ResultSet rs = checkStmt.executeQuery();
            
            // 2. Se não existir, insere o usuário
            if (!rs.next()) {
                insertUsuarioStmt.setString(1, nome);
                insertUsuarioStmt.setString(2, admin.getEmail());
                insertUsuarioStmt.setString(3, admin.getSenha());
                insertUsuarioStmt.setString(4, admin.getTelefone());
                insertUsuarioStmt.executeUpdate();
            }
            
            // 3. Insere o registro de admin
            insertAdminStmt.setString(1, nome);
            insertAdminStmt.setString(2, admin.getCargo());
            
            int affected = insertAdminStmt.executeUpdate();
            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeAdmin(String nome) {
        String sql = "DELETE FROM admins WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            int affected = stmt.executeUpdate();
            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Admin findByNome(String nome) {
        String sql = "SELECT a.nome, a.cargo, u.email, u.telefone, u.senha " +
                     "FROM admins a " +
                     "JOIN usuarios u ON a.nome = u.nome " +
                     "WHERE a.nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String adminNome = rs.getString("nome");
                String cargo = rs.getString("cargo");
                String email = rs.getString("email");
                String telefone = rs.getString("telefone");
                String senha = rs.getString("senha");
                
                // Cria o usuário e o admin
                Usuario usuario = new Usuario();
                usuario.setNome(adminNome);
                usuario.setEmail(email);
                usuario.setTelefone(telefone);
                usuario.setSenha(senha);
                
                Admin admin = new Admin();
                admin.setNome(nome);
                admin.setEmail(email);
                admin.setTelefone(telefone);
                admin.setCargo(cargo);
                
                return admin;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean isAdmin(String nome) {
        String sql = "SELECT COUNT(*) FROM admins WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}