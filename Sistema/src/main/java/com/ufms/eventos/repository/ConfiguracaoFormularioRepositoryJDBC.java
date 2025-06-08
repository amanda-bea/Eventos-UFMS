package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.ConfiguracaoFormulario;
import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.Optional;

public class ConfiguracaoFormularioRepositoryJDBC implements IConfiguracaoFormularioRepository{

    private final AcaoRepositoryJDBC acaoRepository = new AcaoRepositoryJDBC();

    public ConfiguracaoFormulario salvar(ConfiguracaoFormulario configuracao) {
        if (configuracao == null || configuracao.getAcao() == null) {
            System.err.println("Erro: configuração ou ação nula.");
            return null;
        }

        Long acaoId = configuracao.getAcao().getId();
        if (acaoId == null) {
            System.err.println("Erro: ação sem ID.");
            return null;
        }

        String sql = """
            INSERT INTO configuracoes_formulario (acao_id, usar_nome, usar_email, usar_cpf, usar_curso)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (acao_id) DO UPDATE SET
                usar_nome = EXCLUDED.usar_nome,
                usar_email = EXCLUDED.usar_email,
                usar_cpf = EXCLUDED.usar_cpf,
                usar_curso = EXCLUDED.usar_curso
            RETURNING id;
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, acaoId);
            stmt.setBoolean(2, configuracao.isUsarNome());
            stmt.setBoolean(3, configuracao.isUsarEmail());
            stmt.setBoolean(4, configuracao.isUsarCpf());
            stmt.setBoolean(5, configuracao.isUsarCurso());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                configuracao.setId(rs.getLong("id"));
            }

            return configuracao;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<ConfiguracaoFormulario> buscarPorNomeAcao(String nomeAcao) {
        if (nomeAcao == null) return Optional.empty();

        Acao acao = acaoRepository.getAcao(nomeAcao);
        if (acao == null) return Optional.empty();

        String sql = "SELECT * FROM configuracoes_formulario WHERE acao_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, acao.getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ConfiguracaoFormulario config = new ConfiguracaoFormulario();
                config.setId(rs.getLong("id"));
                config.setAcao(acao);
                config.setUsarNome(rs.getBoolean("usar_nome"));
                config.setUsarEmail(rs.getBoolean("usar_email"));
                config.setUsarCpf(rs.getBoolean("usar_cpf"));
                config.setUsarCurso(rs.getBoolean("usar_curso"));
                return Optional.of(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public boolean deletarPorNomeAcao(String nomeAcao) {
        if (nomeAcao == null) return false;

        Acao acao = acaoRepository.getAcao(nomeAcao);
        if (acao == null) return false;

        String sql = "DELETE FROM configuracoes_formulario WHERE acao_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, acao.getId());
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existePorNomeAcao(String nomeAcao) {
        if (nomeAcao == null) return false;

        Acao acao = acaoRepository.getAcao(nomeAcao);
        if (acao == null) return false;

        String sql = "SELECT 1 FROM configuracoes_formulario WHERE acao_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, acao.getId());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
