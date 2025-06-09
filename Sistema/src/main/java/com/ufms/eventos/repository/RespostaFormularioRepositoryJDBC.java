package com.ufms.eventos.repository;

import com.ufms.eventos.util.ConnectionFactory;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.RespostaFormulario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RespostaFormularioRepositoryJDBC implements RespostaFormularioRepository {

    public RespostaFormularioRepositoryJDBC() {
    }

    @Override
    public void salvar(RespostaFormulario resposta) {
        if (resposta == null || resposta.getAcao() == null || resposta.getAcao().getId() == null) {
            return; // Não é possível salvar com dados inválidos
        }
        
        // Primeiro, inserimos os dados principais
        String sqlMain = "INSERT INTO resposta_formulario (id_acao, nome, email, cpf, curso) VALUES (?, ?, ?, ?, ?)";
        
        // Alterado para declarar explicitamente que o método getConnection() lança SQLException
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Inicia transação
            
            try {
                // Insere dados principais
                try (PreparedStatement mainStmt = conn.prepareStatement(sqlMain, Statement.RETURN_GENERATED_KEYS)) {
                    mainStmt.setLong(1, resposta.getAcao().getId());
                    mainStmt.setString(2, resposta.getNome());
                    mainStmt.setString(3, resposta.getEmail());
                    mainStmt.setString(4, resposta.getCpf());
                    mainStmt.setString(5, resposta.getCurso());
                    
                    mainStmt.executeUpdate();
                    
                    // Recupera o ID gerado
                    try (ResultSet generatedKeys = mainStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            resposta.setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Falha ao obter ID da resposta, nenhuma linha afetada.");
                        }
                    }
                }
                
                // Agora insere as respostas extras se existirem
                if (resposta.getRespostasExtras() != null && !resposta.getRespostasExtras().isEmpty()) {
                    String sqlExtras = "INSERT INTO resposta_extras (id_resposta, nome_campo, valor_campo) VALUES (?, ?, ?)";
                    try (PreparedStatement extrasStmt = conn.prepareStatement(sqlExtras)) {
                        for (Map.Entry<String, String> entry : resposta.getRespostasExtras().entrySet()) {
                            extrasStmt.setInt(1, resposta.getId());
                            extrasStmt.setString(2, entry.getKey());
                            extrasStmt.setString(3, entry.getValue());
                            extrasStmt.addBatch();
                        }
                        extrasStmt.executeBatch();
                    }
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RespostaFormulario> listarPorAcao(String acaoNome) {
        if (acaoNome == null || acaoNome.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<RespostaFormulario> respostas = new ArrayList<>();

        String sql = """
                SELECT rf.id, rf.nome, rf.email, rf.cpf, rf.curso, a.id AS id_acao, a.nome AS nome_acao
                FROM resposta_formulario rf
                JOIN acao a ON rf.id_acao = a.id
                WHERE LOWER(a.nome) = LOWER(?)
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, acaoNome);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RespostaFormulario resposta = mapResultSetToRespostaFormulario(rs);
                    
                    // Carrega as respostas extras
                    carregarRespostasExtras(conn, resposta.getId(), resposta);
                    
                    respostas.add(resposta);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return respostas;
    }
    
    @Override
    public List<RespostaFormulario> listarPorAcaoId(Long acaoId) {
        if (acaoId == null) {
            return new ArrayList<>();
        }
        
        List<RespostaFormulario> respostas = new ArrayList<>();

        String sql = """
                SELECT rf.id, rf.nome, rf.email, rf.cpf, rf.curso, a.id AS id_acao, a.nome AS nome_acao
                FROM resposta_formulario rf
                JOIN acao a ON rf.id_acao = a.id
                WHERE a.id = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, acaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RespostaFormulario resposta = mapResultSetToRespostaFormulario(rs);
                    
                    // Carrega as respostas extras
                    carregarRespostasExtras(conn, resposta.getId(), resposta);
                    
                    respostas.add(resposta);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return respostas;
    }
    
    public List<RespostaFormulario> buscarRespostasPorParticipante(Long acaoId, String nomeParticipante) {
        if (acaoId == null || nomeParticipante == null || nomeParticipante.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<RespostaFormulario> respostas = new ArrayList<>();
        
        String sql = """
                SELECT rf.id, rf.nome, rf.email, rf.cpf, rf.curso, a.id AS id_acao, a.nome AS nome_acao
                FROM resposta_formulario rf
                JOIN acao a ON rf.id_acao = a.id
                WHERE a.id = ? AND rf.nome = ?
                """;
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setLong(1, acaoId);
            stmt.setString(2, nomeParticipante);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RespostaFormulario resposta = mapResultSetToRespostaFormulario(rs);
                    
                    // Carrega as respostas extras
                    carregarRespostasExtras(conn, resposta.getId(), resposta);
                    
                    respostas.add(resposta);
                }
            }
             
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return respostas;
    }
    
    @Override
    public boolean excluirPorAcao(Long acaoId) {
        if (acaoId == null) {
            return false;
        }
        
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Primeiro precisamos encontrar os IDs de todas as respostas para essa ação
                List<Integer> respostaIds = new ArrayList<>();
                String sqlFindIds = "SELECT id FROM resposta_formulario WHERE id_acao = ?";
                
                try (PreparedStatement findStmt = conn.prepareStatement(sqlFindIds)) {
                    findStmt.setLong(1, acaoId);
                    
                    try (ResultSet rs = findStmt.executeQuery()) {
                        while (rs.next()) {
                            respostaIds.add(rs.getInt("id"));
                        }
                    }
                }
                
                // Excluir as respostas extras primeiro
                if (!respostaIds.isEmpty()) {
                    String sqlDeleteExtras = "DELETE FROM resposta_extras WHERE id_resposta = ?";
                    try (PreparedStatement deleteExtrasStmt = conn.prepareStatement(sqlDeleteExtras)) {
                        for (Integer respostaId : respostaIds) {
                            deleteExtrasStmt.setInt(1, respostaId);
                            deleteExtrasStmt.addBatch();
                        }
                        deleteExtrasStmt.executeBatch();
                    }
                }
                
                // Agora exclui as respostas principais
                String sqlDeleteMain = "DELETE FROM resposta_formulario WHERE id_acao = ?";
                try (PreparedStatement deleteMainStmt = conn.prepareStatement(sqlDeleteMain)) {
                    deleteMainStmt.setLong(1, acaoId);
                    deleteMainStmt.executeUpdate();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Carrega as respostas extras para um formulário específico.
     */
    private void carregarRespostasExtras(Connection conn, Integer respostaId, RespostaFormulario resposta) throws SQLException {
        String sql = "SELECT nome_campo, valor_campo FROM resposta_extras WHERE id_resposta = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, respostaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nomeCampo = rs.getString("nome_campo");
                    String valorCampo = rs.getString("valor_campo");
                    resposta.getRespostasExtras().put(nomeCampo, valorCampo);
                }
            }
        }
    }
    
    /**
     * Mapeia um ResultSet para um objeto RespostaFormulario.
     */
    private RespostaFormulario mapResultSetToRespostaFormulario(ResultSet rs) throws SQLException {
        Acao acao = new Acao();
        acao.setId(rs.getLong("id_acao"));
        acao.setNome(rs.getString("nome_acao"));

        RespostaFormulario resposta = new RespostaFormulario();
        resposta.setId(rs.getInt("id"));
        resposta.setNome(rs.getString("nome"));
        resposta.setEmail(rs.getString("email"));
        resposta.setCpf(rs.getString("cpf"));
        resposta.setCurso(rs.getString("curso"));
        resposta.setAcao(acao);

        return resposta;
    }
}