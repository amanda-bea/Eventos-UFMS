package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Date;
import java.util.*;

/**
 * Implementação que acessa o banco de dados para operações com ações.
 */
public class AcaoRepositoryJDBC implements AcaoRepository {

    private final EventoRepositoryJDBC eventoRepository;

    public AcaoRepositoryJDBC() {
        this.eventoRepository = new EventoRepositoryJDBC();
    }

    @Override
    public HashSet<Acao> getAcoes() {
        HashSet<Acao> acoes = new HashSet<>();
        String sql = "SELECT * FROM acoes";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                acoes.add(mapResultSetToAcao(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return acoes;
    }

    @Override
    public boolean addAcao(Acao acao) {
        String sql = "INSERT INTO acoes (evento_id, nome, data, descricao, local, horario_inicio, horario_fim, " +
                     "departamento, contato, modalidade, link, status, capacidade, mensagem_rejeicao) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, acao.getEvento().getId());
            pstmt.setString(2, acao.getNome());
            pstmt.setDate(3, Date.valueOf(acao.getData()));
            pstmt.setString(4, acao.getDescricao());
            pstmt.setString(5, acao.getLocal());
            pstmt.setTime(6, Time.valueOf(acao.getHorarioInicio()));
            pstmt.setTime(7, Time.valueOf(acao.getHorarioFim()));
            pstmt.setString(8, acao.getDepartamento().name());
            pstmt.setString(9, acao.getContato());
            pstmt.setString(10, acao.getModalidade());
            pstmt.setString(11, acao.getLink());
            pstmt.setString(12, acao.getStatus());
            pstmt.setInt(13, acao.getCapacidade());
            pstmt.setString(14, acao.getMensagemRejeicao());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        acao.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeAcao(Acao acao) {
        if (acao.getId() != null) {
            String sql = "DELETE FROM acoes WHERE id = ?";

            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, acao.getId());
                return pstmt.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (acao.getNome() != null) {
            return deleteAcao(acao.getNome());
        }
        return false;
    }

    @Override
    public boolean updateAcao(Acao acao) {
        String sql = "UPDATE acoes SET evento_id = ?, nome = ?, data = ?, descricao = ?, local = ?, " +
                     "horario_inicio = ?, horario_fim = ?, departamento = ?, contato = ?, " +
                     "modalidade = ?, link = ?, status = ?, capacidade = ?, mensagem_rejeicao = ? " +
                     "WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, acao.getEvento().getId());
            pstmt.setString(2, acao.getNome());
            pstmt.setDate(3, Date.valueOf(acao.getData()));
            pstmt.setString(4, acao.getDescricao());
            pstmt.setString(5, acao.getLocal());
            pstmt.setTime(6, Time.valueOf(acao.getHorarioInicio()));
            pstmt.setTime(7, Time.valueOf(acao.getHorarioFim()));
            pstmt.setString(8, acao.getDepartamento().name());
            pstmt.setString(9, acao.getContato());
            pstmt.setString(10, acao.getModalidade());
            pstmt.setString(11, acao.getLink());
            pstmt.setString(12, acao.getStatus());
            pstmt.setInt(13, acao.getCapacidade());
            pstmt.setString(14, acao.getMensagemRejeicao());
            pstmt.setLong(15, acao.getId());

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Acao getAcao(String nome) {
        String sql = "SELECT * FROM acoes WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAcao(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteAcao(String nome) {
        String sql = "DELETE FROM acoes WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Acao findById(Long id) {
        String sql = "SELECT * FROM acoes WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAcao(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Acao> findByEventoId(Long eventoId) {
        List<Acao> acoes = new ArrayList<>();
        
        // Verifica se o ID é nulo antes de prosseguir
        if (eventoId == null) {
            System.err.println("AVISO: Tentativa de buscar ações com eventoId nulo");
            return acoes; // Retorna lista vazia em vez de causar exceção
        }
        
        String sql = "SELECT id, nome, descricao, data, status, evento_id, local, horario_inicio, " +
             "horario_fim, departamento, contato, modalidade, link, capacidade, mensagem_rejeicao " +
             "FROM acoes WHERE evento_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, eventoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Acao acao = mapResultSetToAcao(rs);
                acoes.add(acao);
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar ações por evento ID: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro ao buscar ações por evento ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return acoes;
    }

    /**
     * Converte ResultSet em objeto Acao
     */
    private Acao mapResultSetToAcao(ResultSet rs) throws SQLException {
        Acao acao = new Acao();

        acao.setId(rs.getLong("id"));
        acao.setNome(rs.getString("nome"));
        
        Date sqlDate = rs.getDate("data");
        if (sqlDate != null) {
            acao.setData(sqlDate.toLocalDate());
        }
        
        acao.setDescricao(rs.getString("descricao"));
        acao.setLocal(rs.getString("local"));
        
        Time startTime = rs.getTime("horario_inicio");
        if (startTime != null) {
            acao.setHorarioInicio(startTime.toLocalTime());
        }
        
        Time endTime = rs.getTime("horario_fim");
        if (endTime != null) {
            acao.setHorarioFim(endTime.toLocalTime());
        }
        
        acao.setContato(rs.getString("contato"));
        acao.setModalidade(rs.getString("modalidade"));
        acao.setLink(rs.getString("link"));
        acao.setStatus(rs.getString("status"));
        acao.setCapacidade(rs.getInt("capacidade"));
        acao.setMensagemRejeicao(rs.getString("mensagem_rejeicao"));

        // Relacionamento com evento
        Long eventoId = rs.getLong("evento_id");
        try {
            Evento evento = eventoRepository.buscarPorId(eventoId);
            acao.setEvento(evento);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Departamento - converte string para enum
        String departamentoNome = rs.getString("departamento");
        if (departamentoNome != null && !departamentoNome.isEmpty()) {
            try {
                acao.setDepartamento(Departamento.valueOf(departamentoNome));
            } catch (IllegalArgumentException e) {
                System.err.println("Erro ao converter departamento: " + departamentoNome);
            }
        }

        return acao;
    }
}