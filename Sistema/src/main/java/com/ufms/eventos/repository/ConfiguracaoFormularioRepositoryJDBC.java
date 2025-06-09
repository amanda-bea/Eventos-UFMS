package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.ConfiguracaoFormulario;
import com.ufms.eventos.util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação JDBC do repositório de configurações de formulário.
 */
public class ConfiguracaoFormularioRepositoryJDBC implements ConfiguracaoFormularioRepository {

    private final AcaoRepositoryJDBC acaoRepository;
    
    public ConfiguracaoFormularioRepositoryJDBC() {
        this.acaoRepository = new AcaoRepositoryJDBC();
    }

    @Override
    public ConfiguracaoFormulario salvar(ConfiguracaoFormulario configuracao) {
        if (configuracao == null || configuracao.getAcao() == null || 
            configuracao.getAcao().getNome() == null || configuracao.getAcao().getNome().trim().isEmpty() ||
            configuracao.getAcao().getId() == null) {
            System.err.println("ConfiguracaoFormularioRepository: Tentativa de salvar configuração nula ou inválida.");
            return null;
        }
        
        // Verifica se já existe uma configuração para essa ação
        String checkSql = "SELECT id FROM configuracoes_formulario WHERE acao_id = ?";
        String insertSql = "INSERT INTO configuracoes_formulario (acao_id, usar_nome, usar_email, usar_cpf, usar_curso) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE configuracoes_formulario SET usar_nome = ?, usar_email = ?, usar_cpf = ?, usar_curso = ? WHERE acao_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setLong(1, configuracao.getAcao().getId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Já existe uma configuração, faz um update
                Long configId = rs.getLong("id");
                
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setBoolean(1, configuracao.isUsarNome());
                    updateStmt.setBoolean(2, configuracao.isUsarEmail());
                    updateStmt.setBoolean(3, configuracao.isUsarCpf());
                    updateStmt.setBoolean(4, configuracao.isUsarCurso());
                    updateStmt.setLong(5, configuracao.getAcao().getId());
                    
                    updateStmt.executeUpdate();
                    configuracao.setId(configId); // Mantém o mesmo ID
                }
                
                // Atualiza campos personalizados
                atualizarCamposPersonalizados(conn, configId, configuracao.getCamposPersonalizados());
                
            } else {
                // Não existe, faz um insert
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setLong(1, configuracao.getAcao().getId());
                    insertStmt.setBoolean(2, configuracao.isUsarNome());
                    insertStmt.setBoolean(3, configuracao.isUsarEmail());
                    insertStmt.setBoolean(4, configuracao.isUsarCpf());
                    insertStmt.setBoolean(5, configuracao.isUsarCurso());
                    
                    insertStmt.executeUpdate();
                    
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            Long configId = generatedKeys.getLong(1);
                            configuracao.setId(configId);
                            
                            // Insere campos personalizados
                            inserirCamposPersonalizados(conn, configId, configuracao.getCamposPersonalizados());
                        }
                    }
                }
            }
            
            return configuracao;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<ConfiguracaoFormulario> buscarPorNomeAcao(String nomeAcao) {
        if (nomeAcao == null || nomeAcao.trim().isEmpty()) {
            return Optional.empty();
        }
        
        // Primeiro encontramos a ação pelo nome
        Acao acao = acaoRepository.getAcao(nomeAcao);
        if (acao == null) {
            return Optional.empty();
        }
        
        // Depois buscamos a configuração pelo ID da ação
        return buscarPorAcaoId(acao.getId());
    }

    public Optional<ConfiguracaoFormulario> buscarPorAcaoId(Long acaoId) {
        if (acaoId == null) {
            return Optional.empty();
        }
        
        String sql = "SELECT id, acao_id, usar_nome, usar_email, usar_cpf, usar_curso " +
                     "FROM configuracoes_formulario WHERE acao_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, acaoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Criar a configuração
                ConfiguracaoFormulario config = new ConfiguracaoFormulario();
                config.setId(rs.getLong("id"));
                config.setUsarNome(rs.getBoolean("usar_nome"));
                config.setUsarEmail(rs.getBoolean("usar_email"));
                config.setUsarCpf(rs.getBoolean("usar_cpf"));
                config.setUsarCurso(rs.getBoolean("usar_curso"));
                
                // Buscar a ação correspondente
                Acao acao = acaoRepository.findById(acaoId);
                config.setAcao(acao);
                
                // Buscar campos personalizados
                config.setCamposPersonalizados(buscarCamposPersonalizados(conn, config.getId()));
                
                return Optional.of(config);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }

    @Override
    public boolean deletarPorNomeAcao(String nomeAcao) {
        if (nomeAcao == null || nomeAcao.trim().isEmpty()) {
            return false;
        }
        
        // Primeiro encontramos a ação pelo nome
        Acao acao = acaoRepository.getAcao(nomeAcao);
        if (acao == null) {
            return false;
        }
        
        // Depois deletamos pelo ID da ação
        return deletarPorAcaoId(acao.getId());
    }

    public boolean deletarPorAcaoId(Long acaoId) {
        if (acaoId == null) {
            return false;
        }
        
        String selectSql = "SELECT id FROM configuracoes_formulario WHERE acao_id = ?";
        String deleteFieldsSql = "DELETE FROM campos_personalizados WHERE configuracao_id = ?";
        String deleteConfigSql = "DELETE FROM configuracoes_formulario WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            
            selectStmt.setLong(1, acaoId);
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                Long configId = rs.getLong("id");
                
                // Primeiro deletamos os campos personalizados (integridade referencial)
                try (PreparedStatement deleteFieldsStmt = conn.prepareStatement(deleteFieldsSql)) {
                    deleteFieldsStmt.setLong(1, configId);
                    deleteFieldsStmt.executeUpdate();
                }
                
                // Depois deletamos a configuração
                try (PreparedStatement deleteConfigStmt = conn.prepareStatement(deleteConfigSql)) {
                    deleteConfigStmt.setLong(1, configId);
                    return deleteConfigStmt.executeUpdate() > 0;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean existePorNomeAcao(String nomeAcao) {
        if (nomeAcao == null || nomeAcao.trim().isEmpty()) {
            return false;
        }
        
        // Primeiro encontramos a ação pelo nome
        Acao acao = acaoRepository.getAcao(nomeAcao);
        if (acao == null) {
            return false;
        }
        
        // Depois verificamos pelo ID da ação
        return existePorAcaoId(acao.getId());
    }

    public boolean existePorAcaoId(Long acaoId) {
        if (acaoId == null) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM configuracoes_formulario WHERE acao_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, acaoId);
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
    
    /**
     * Busca os campos personalizados de uma configuração.
     */
    private List<String> buscarCamposPersonalizados(Connection conn, Long configId) throws SQLException {
        List<String> campos = new ArrayList<>();
        String sql = "SELECT nome_campo FROM campos_personalizados WHERE configuracao_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, configId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                campos.add(rs.getString("nome_campo"));
            }
        }
        
        return campos;
    }
    
    /**
     * Insere os campos personalizados de uma configuração.
     */
    private void inserirCamposPersonalizados(Connection conn, Long configId, List<String> campos) throws SQLException {
        if (campos == null || campos.isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO campos_personalizados (configuracao_id, nome_campo) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String campo : campos) {
                if (campo != null && !campo.trim().isEmpty()) {
                    stmt.setLong(1, configId);
                    stmt.setString(2, campo);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
    
    /**
     * Atualiza os campos personalizados de uma configuração.
     */
    private void atualizarCamposPersonalizados(Connection conn, Long configId, List<String> campos) throws SQLException {
        // Primeiro removemos todos os campos existentes
        String deleteSql = "DELETE FROM campos_personalizados WHERE configuracao_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setLong(1, configId);
            stmt.executeUpdate();
        }
        
        // Depois inserimos os novos campos
        inserirCamposPersonalizados(conn, configId, campos);
    }
}