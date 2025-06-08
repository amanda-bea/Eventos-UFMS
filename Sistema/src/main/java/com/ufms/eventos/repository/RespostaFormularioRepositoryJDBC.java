package com.ufms.eventos.repository;

import com.ufms.eventos.util.ConnectionFactory;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.RespostaFormulario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RespostaFormularioRepositoryJDBC implements IRespostaFormularioRepository {

    public RespostaFormularioRepositoryJDBC() {
        // Construtor padrão
    }

    @Override
    public void salvar(RespostaFormulario resposta) {
        String sql = "INSERT INTO resposta_formulario (id_acao, pergunta, resposta) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, resposta.getAcao().getId());
            stmt.setString(2, resposta.getPergunta());
            stmt.setString(3, resposta.getResposta());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RespostaFormulario> listarPorAcao(String acaoNome) {
        List<RespostaFormulario> respostas = new ArrayList<>();

        String sql = """
                SELECT rf.pergunta, rf.resposta, a.id AS id_acao, a.nome AS nome_acao
                FROM resposta_formulario rf
                JOIN acao a ON rf.id_acao = a.id
                WHERE LOWER(a.nome) = LOWER(?)
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, acaoNome);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Acao acao = new Acao();
                    acao.setId(rs.getLong("id_acao"));
                    acao.setNome(rs.getString("nome_acao"));

                    RespostaFormulario resposta = new RespostaFormulario();
                    resposta.setAcao(acao);
                    resposta.setPergunta(rs.getString("pergunta"));
                    resposta.setResposta(rs.getString("resposta"));

                    respostas.add(resposta);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return respostas;
    }
}
