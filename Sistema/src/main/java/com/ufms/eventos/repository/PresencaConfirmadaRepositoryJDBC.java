package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.HashSet;

/**
 * Implementação JDBC do repositório de presenças confirmadas.
 */
public class PresencaConfirmadaRepositoryJDBC implements PresencaConfirmadaRepository {

    private AcaoRepositoryJDBC acaoRepository;
    private UsuarioRepositoryJDBC usuarioRepository;

    public PresencaConfirmadaRepositoryJDBC() {
        this.acaoRepository = new AcaoRepositoryJDBC();
        this.usuarioRepository = new UsuarioRepositoryJDBC();
    }

    @Override
    public boolean addPresencaConfirmada(PresencaConfirmada presenca) {
        if (presenca == null || presenca.getUsuario() == null || presenca.getAcao() == null) {
            return false;
        }
        
        // Adaptação para usar o nome do usuário e o id da ação conforme schema do DB
        String sql = "INSERT INTO presencas_confirmadas (usuario_nome, acao_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, presenca.getUsuario().getNome());
            stmt.setLong(2, presenca.getAcao().getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removePresencaConfirmada(PresencaConfirmada presenca) {
        if (presenca == null || presenca.getUsuario() == null || presenca.getAcao() == null) {
            return false;
        }
        
        // Adaptação para usar o nome do usuário e o id da ação
        String sql = "DELETE FROM presencas_confirmadas WHERE usuario_nome = ? AND acao_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, presenca.getUsuario().getNome());
            stmt.setLong(2, presenca.getAcao().getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String usuarioNome, Long acaoId) {
        if (usuarioNome == null || usuarioNome.trim().isEmpty() || acaoId == null) {
            return false;
        }
        
        // Utilizando a tabela presencas_confirmadas conforme criada no ConnectionFactory
        String sql = "DELETE FROM presencas_confirmadas WHERE usuario_nome = ? AND acao_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioNome);
            stmt.setLong(2, acaoId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Presença cancelada com sucesso para usuário: " + usuarioNome + " na ação ID: " + acaoId);
                return true;
            } else {
                System.out.println("Nenhum registro encontrado para cancelar a presença do usuário: " + usuarioNome + " na ação ID: " + acaoId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao cancelar presença: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public HashSet<PresencaConfirmada> getPresencasConfirmadas() {
        HashSet<PresencaConfirmada> presencas = new HashSet<>();
        String sql = "SELECT p.usuario_nome, p.acao_id, p.data_confirmacao " +
                     "FROM presencas_confirmadas p";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nomeUsuario = rs.getString("usuario_nome");
                Long acaoId = rs.getLong("acao_id");
                Timestamp dataConfirmacao = rs.getTimestamp("data_confirmacao");

                Usuario usuario = usuarioRepository.getUsuarioPorNome(nomeUsuario);
                Acao acao = acaoRepository.findById(acaoId);
                
                if (usuario != null && acao != null) {
                    PresencaConfirmada presenca = new PresencaConfirmada(usuario, acao);
                    presenca.setDataConfirmacao(dataConfirmacao != null ? dataConfirmacao.toLocalDateTime() : null);
                    presencas.add(presenca);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return presencas;
    }

    @Override
    public HashSet<PresencaConfirmada> getPresencasPorUsuario(String nomeUsuario) {
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            return new HashSet<>();
        }
        
        HashSet<PresencaConfirmada> presencas = new HashSet<>();
        String sql = "SELECT p.usuario_nome, p.acao_id, p.data_confirmacao " +
                     "FROM presencas_confirmadas p " +
                     "WHERE p.usuario_nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomeUsuario);
            ResultSet rs = stmt.executeQuery();

            Usuario usuario = usuarioRepository.getUsuarioPorNome(nomeUsuario);
            if (usuario == null) {
                return presencas; // Retorna conjunto vazio se o usuário não existir
            }
            
            while (rs.next()) {
                Long acaoId = rs.getLong("acao_id");
                Timestamp dataConfirmacao = rs.getTimestamp("data_confirmacao");
                
                Acao acao = acaoRepository.findById(acaoId);
                if (acao != null) {
                    PresencaConfirmada presenca = new PresencaConfirmada(usuario, acao);
                    presenca.setDataConfirmacao(dataConfirmacao != null ? dataConfirmacao.toLocalDateTime() : null);
                    presencas.add(presenca);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return presencas;
    }

    @Override
    public HashSet<PresencaConfirmada> getPresencasPorAcao(Long acaoId) {
        if (acaoId == null) {
            return new HashSet<>();
        }
        
        HashSet<PresencaConfirmada> presencas = new HashSet<>();
        String sql = "SELECT p.usuario_nome, p.acao_id, p.data_confirmacao " +
                     "FROM presencas_confirmadas p " +
                     "WHERE p.acao_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, acaoId);
            ResultSet rs = stmt.executeQuery();

            Acao acao = acaoRepository.findById(acaoId);
            if (acao == null) {
                return presencas; // Retorna conjunto vazio se a ação não existir
            }
            
            while (rs.next()) {
                String nomeUsuario = rs.getString("usuario_nome");
                Timestamp dataConfirmacao = rs.getTimestamp("data_confirmacao");
                
                Usuario usuario = usuarioRepository.getUsuarioPorNome(nomeUsuario);
                if (usuario != null) {
                    PresencaConfirmada presenca = new PresencaConfirmada(usuario, acao);
                    presenca.setDataConfirmacao(dataConfirmacao != null ? dataConfirmacao.toLocalDateTime() : null);
                    presencas.add(presenca);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return presencas;
    }

    @Override
    public boolean verificarPresenca(String nomeUsuario, Long acaoId) {
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty() || acaoId == null) {
            return false;
        }
        
        String sql = "SELECT 1 FROM presencas_confirmadas WHERE usuario_nome = ? AND acao_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomeUsuario);
            stmt.setLong(2, acaoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou algum registro
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public int contarPresencasPorAcao(Long acaoId) {
        if (acaoId == null) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM presencas_confirmadas WHERE acao_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, acaoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}