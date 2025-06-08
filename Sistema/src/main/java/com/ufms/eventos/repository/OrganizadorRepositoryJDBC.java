package com.ufms.eventos.repository;

import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.HashSet;

public class OrganizadorRepositoryJDBC implements IOrganizadorRepository{

    public HashSet<Organizador> getOrganizadores() {
        HashSet<Organizador> organizadores = new HashSet<>();
        String sql = "SELECT * FROM organizadores";

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

    public boolean addOrganizador(Organizador organizador) {
        String sql = "INSERT INTO organizadores (nome) VALUES (?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, organizador.getNome());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                organizador.setId(rs.getLong("id"));
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeOrganizador(Organizador organizador) {
        String sql = "DELETE FROM organizadores WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, organizador.getId());
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Organizador getOrganizador(String nome) {
        String sql = "SELECT * FROM organizadores WHERE LOWER(nome) = LOWER(?) LIMIT 1";

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

    public Organizador salvar(Organizador organizador) {
        if (organizador == null) return null;

        Organizador existente = getOrganizador(organizador.getNome());

        if (existente != null) {
            // Atualiza
            String sql = "UPDATE organizadores SET nome = ? WHERE id = ?";

            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, organizador.getNome());
                stmt.setLong(2, existente.getId());

                if (stmt.executeUpdate() > 0) {
                    organizador.setId(existente.getId());
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

    public Organizador findById(Long id) {
        String sql = "SELECT * FROM organizadores WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrganizador(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Organizador mapResultSetToOrganizador(ResultSet rs) throws SQLException {
        Organizador o = new Organizador();
        o.setId(rs.getLong("id"));
        o.setNome(rs.getString("nome"));
        return o;
    }
}
