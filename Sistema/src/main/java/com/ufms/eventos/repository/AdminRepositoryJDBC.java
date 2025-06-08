package com.ufms.eventos.repository;

import com.ufms.eventos.model.Admin;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.HashSet;

public class AdminRepositoryJDBC implements IAdminRepository {
    
    /**
     * Retorna todos os administradores cadastrados no banco de dados.
     */
    public HashSet<Admin> getAdmins() {
        HashSet<Admin> admins = new HashSet<>();

        String sql = "SELECT a.id, a.usuario_id, a.cargo FROM admins a";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getLong("id"));
                admin.setUsuarioId(rs.getLong("usuario_id"));
                admin.setCargo(rs.getString("cargo"));
                admins.add(admin);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return admins;
    }

    /**
     * Insere um novo admin no banco de dados.
     * Retorna true se inserido com sucesso.
     */
    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admins (usuario_id, cargo) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, admin.getUsuarioId());
            stmt.setString(2, admin.getCargo());

            int affected = stmt.executeUpdate();
            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Remove um admin com base no ID do admin.
     */
    public boolean removeAdmin(Admin admin) {
        String sql = "DELETE FROM admins WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, admin.getId());

            int affected = stmt.executeUpdate();
            return affected > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
