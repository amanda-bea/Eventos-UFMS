package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.RespostaFormulario;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PresencaConfirmadaRepositoryJDBC implements IPresencaConfirmadaRepository {

    private AcaoRepositoryJDBC acaoRepository;
    private UsuarioRepositoryJDBC usuarioRepository;

    public PresencaConfirmadaRepositoryJDBC() {
        this.acaoRepository = new AcaoRepositoryJDBC();
        this.usuarioRepository = new UsuarioRepositoryJDBC();
    }

    @Override
    public boolean addPresencaConfirmada(PresencaConfirmada presenca) {
        String sql = "INSERT INTO presenca_confirmada (id_usuario, id_acao) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, presenca.getUsuario().getId());
            stmt.setLong(2, presenca.getAcao().getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // Em produção, usar logger
            return false;
        }
    }

    @Override
    public boolean removePresencaConfirmada(PresencaConfirmada presenca) {
        String sql = "DELETE FROM presenca_confirmada WHERE id_usuario = ? AND id_acao = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, presenca.getUsuario().getId());
            stmt.setLong(2, presenca.getAcao().getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // Em produção, usar logger
            return false;
        }
    }

    @Override
    public HashSet<PresencaConfirmada> getPresencasConfirmadas() {
        HashSet<PresencaConfirmada> presencas = new HashSet<>();
        String sql = "SELECT id_usuario, id_acao FROM presenca_confirmada";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long idUsuario = rs.getLong("id_usuario");
                Long idAcao = rs.getLong("id_acao");

                Usuario usuario = usuarioRepository.findById(idUsuario);
                Acao acao = acaoRepository.findById(idAcao);
                
                if (usuario != null && acao != null) {
                    PresencaConfirmada presenca = new PresencaConfirmada(usuario, acao);
                    presencas.add(presenca);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return presencas;
    }

    @Override
