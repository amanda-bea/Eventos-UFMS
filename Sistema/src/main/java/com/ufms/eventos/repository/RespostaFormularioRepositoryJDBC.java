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
        
        // Primeiro, inserimos os dados principais - CORRIGIDO nome da tabela para respostas_formulario
        String sqlMain = "INSERT INTO respostas_formulario (acao_id, nome, email, cpf, curso) VALUES (?, ?, ?, ?, ?)";
        
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
                
                // Agora insere as respostas extras se existirem - CORRIGIDO nome da tabela para respostas_extras
                if (resposta.getRespostasExtras() != null && !resposta.getRespostasExtras().isEmpty()) {
                    // CORRIGIDO nome da tabela e nomes de colunas
                    String sqlExtras = "INSERT INTO respostas_extras (resposta_id, nome_campo, valor_campo) VALUES (?, ?, ?)";
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
    public List<RespostaFormulario> listarPorAcaoId(Long acaoId) {
        if (acaoId == null) {
            return new ArrayList<>();
        }
        
        List<RespostaFormulario> respostas = new ArrayList<>();

        // CORRIGIDO nome da tabela e nomes de colunas
        String sql = """
                SELECT rf.id, rf.nome, rf.email, rf.cpf, rf.curso, a.id AS acao_id, a.nome AS nome_acao
                FROM respostas_formulario rf
                JOIN acoes a ON rf.acao_id = a.id
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
        
        // CORRIGIDO nome da tabela e nomes de colunas
        String sql = """
                SELECT rf.id, rf.nome, rf.email, rf.cpf, rf.curso, a.id AS acao_id, a.nome AS nome_acao
                FROM respostas_formulario rf
                JOIN acoes a ON rf.acao_id = a.id
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
    
    /**
     * Carrega as respostas extras para um formulário específico.
     */
    private void carregarRespostasExtras(Connection conn, Integer respostaId, RespostaFormulario resposta) throws SQLException {
        // CORRIGIDO nome da tabela e nomes de colunas
        String sql = "SELECT nome_campo, valor_campo FROM respostas_extras WHERE resposta_id = ?";
        
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
        // CORRIGIDO nome da coluna de acao_id
        acao.setId(rs.getLong("acao_id"));
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