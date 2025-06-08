package com.ufms.eventos.repository;

import com.ufms.eventos.model.*;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

public class EventoRepositoryJDBC implements IEventoRepository {

    public boolean addEvento(Evento evento) {
        String sql = """
            INSERT INTO eventos (nome, data_inicio, data_fim, descricao, organizador_id, departamento_id, categoria_id, imagem, link, status, mensagem_rejeicao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setDate(2, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(3, Date.valueOf(evento.getDataFim()));
            stmt.setString(4, evento.getDescricao());
            stmt.setLong(5, evento.getOrganizador().getId());
            stmt.setLong(6, evento.getDepartamento().getId());
            stmt.setLong(7, evento.getCategoria().getId());
            stmt.setString(8, evento.getImagem());
            stmt.setString(9, evento.getLink());
            stmt.setString(10, evento.getStatus());
            stmt.setString(11, evento.getMensagemRejeicao());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                evento.setId(rs.getLong("id"));
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeEvento(Evento evento) {
        String sql = "DELETE FROM eventos WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, evento.getId());
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEvento(Evento evento) {
        String sql = """
            UPDATE eventos SET
                nome = ?, data_inicio = ?, data_fim = ?, descricao = ?,
                organizador_id = ?, departamento_id = ?, categoria_id = ?,
                imagem = ?, link = ?, status = ?, mensagem_rejeicao = ?
            WHERE id = ?
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setDate(2, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(3, Date.valueOf(evento.getDataFim()));
            stmt.setString(4, evento.getDescricao());
            stmt.setLong(5, evento.getOrganizador().getId());
            stmt.setLong(6, evento.getDepartamento().getId());
            stmt.setLong(7, evento.getCategoria().getId());
            stmt.setString(8, evento.getImagem());
            stmt.setString(9, evento.getLink());
            stmt.setString(10, evento.getStatus());
            stmt.setString(11, evento.getMensagemRejeicao());
            stmt.setLong(12, evento.getId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Evento getEvento(String nome) {
        String sql = "SELECT * FROM eventos WHERE LOWER(nome) = LOWER(?) LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return construirEvento(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Evento findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM eventos WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return construirEvento(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashSet<Evento> getEventos() {
        HashSet<Evento> eventos = new HashSet<>();
        String sql = "SELECT * FROM eventos";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                eventos.add(construirEvento(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventos;
    }

    public HashSet<Evento> getSolicitacoes() {
        HashSet<Evento> solicitacoes = new HashSet<>();
        String sql = "SELECT * FROM eventos WHERE LOWER(status) = LOWER('Aguardando aprovação')";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                solicitacoes.add(construirEvento(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return solicitacoes;
    }

    private Evento construirEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setNome(rs.getString("nome"));
        evento.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        evento.setDataFim(rs.getDate("data_fim").toLocalDate());
        evento.setDescricao(rs.getString("descricao"));
        evento.setImagem(rs.getString("imagem"));
        evento.setLink(rs.getString("link"));
        evento.setStatus(rs.getString("status"));
        evento.setMensagemRejeicao(rs.getString("mensagem_rejeicao"));

        // Assume-se que você tenha métodos como `OrganizadorRepository.findById`, etc.
        evento.setOrganizador(new Organizador(rs.getLong("organizador_id")));
        evento.setDepartamento(new Departamento(rs.getLong("departamento_id")));
        evento.setCategoria(new Categoria(rs.getLong("categoria_id")));

        return evento;
    }
}
