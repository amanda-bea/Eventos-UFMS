package com.ufms.eventos.repository;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.*;

public class AcaoRepositoryJDBC implements IAcaoRepository {

    private final EventoRepository eventoRepository;

    // Construtor que instancia o repositório de eventos
    public AcaoRepositoryJDBC() {
        this.eventoRepository = new EventoRepository();
    }

    // Adiciona uma nova ação no banco de dados
    public boolean add(Acao acao) {
        String sql = "INSERT INTO acoes (evento_id, nome, data, descricao, local, horario_inicio, horario_fim, " +
                     "departamento_id, contato, modalidade, imagem, link, status, capacidade, mensagem_rejeicao) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, (SELECT id FROM departamentos WHERE nome = ?), ?, ?, ?, ?, ?, ?, ?)";

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
            pstmt.setString(11, acao.getImagem());
            pstmt.setString(12, acao.getLink());
            pstmt.setString(13, acao.getStatus());
            pstmt.setInt(14, acao.getCapacidade());
            pstmt.setString(15, acao.getMensagemRejeicao());

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Atualiza os dados de uma ação existente, identificada pelo ID
    public boolean update(Acao acao) {
        String sql = "UPDATE acoes SET evento_id = ?, data = ?, descricao = ?, local = ?, horario_inicio = ?, " +
                     "horario_fim = ?, departamento_id = (SELECT id FROM departamentos WHERE nome = ?), contato = ?, " +
                     "modalidade = ?, imagem = ?, link = ?, status = ?, capacidade = ?, mensagem_rejeicao = ? " +
                     "WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, acao.getEvento().getId());
            pstmt.setDate(2, Date.valueOf(acao.getData()));
            pstmt.setString(3, acao.getDescricao());
            pstmt.setString(4, acao.getLocal());
            pstmt.setTime(5, Time.valueOf(acao.getHorarioInicio()));
            pstmt.setTime(6, Time.valueOf(acao.getHorarioFim()));
            pstmt.setString(7, acao.getDepartamento().name());
            pstmt.setString(8, acao.getContato());
            pstmt.setString(9, acao.getModalidade());
            pstmt.setString(10, acao.getImagem());
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

    // Remove uma ação do banco de dados com base no nome
    public boolean deleteByNome(String nome) {
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
    
    // Remove uma ação do banco de dados com base no ID
    public boolean delete(Long id) {
        String sql = "DELETE FROM acoes WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Busca uma ação pelo seu ID
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
    
    // Busca uma ação pelo seu nome
    public Acao findByNome(String nome) {
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

    // Busca todas as ações associadas a um evento específico
    public List<Acao> findByEventoId(Long eventoId) {
        List<Acao> acoes = new ArrayList<>();
        String sql = "SELECT * FROM acoes WHERE evento_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, eventoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                acoes.add(mapResultSetToAcao(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acoes;
    }

    // Retorna todas as ações do banco de dados
    public Set<Acao> findAll() {
        Set<Acao> acoes = new HashSet<>();
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

    // Converte uma linha do ResultSet em um objeto Acao
    private Acao mapResultSetToAcao(ResultSet rs) throws SQLException {
        Acao acao = new Acao();

        acao.setId(rs.getLong("id"));
        acao.setNome(rs.getString("nome"));
        acao.setData(rs.getDate("data").toLocalDate());
        acao.setDescricao(rs.getString("descricao"));
        acao.setLocal(rs.getString("local"));
        acao.setHorarioInicio(rs.getTime("horario_inicio").toLocalTime());
        acao.setHorarioFim(rs.getTime("horario_fim").toLocalTime());
        acao.setContato(rs.getString("contato"));
        acao.setModalidade(rs.getString("modalidade"));
        acao.setImagem(rs.getString("imagem"));
        acao.setLink(rs.getString("link"));
        acao.setStatus(rs.getString("status"));
        acao.setCapacidade(rs.getInt("capacidade"));
        acao.setMensagemRejeicao(rs.getString("mensagem_rejeicao"));

        // Relacionamento com evento
        Long eventoId = rs.getLong("evento_id");
        Evento evento = eventoRepository.findById(eventoId);
        acao.setEvento(evento);

        // Departamento
        Long departamentoId = rs.getLong("departamento_id");
        String depNome = getDepartamentoNomeById(departamentoId);
        acao.setDepartamento(Departamento.valueOf(depNome));

        return acao;
    }

    // Busca o nome de um departamento pelo seu ID
    private String getDepartamentoNomeById(Long id) throws SQLException {
        String sql = "SELECT nome FROM departamentos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nome");
            }
        }
        return null;
    }
    
    // Métodos de compatibilidade com a interface IAcaoRepository
    
    @Override
    public HashSet<Acao> getAcoes() {
        return new HashSet<>(findAll());
    }
    
    @Override
    public boolean addAcao(Acao acao) {
        return add(acao);
    }
    
    @Override
    public boolean updateAcao(Acao acao) {
        return update(acao);
    }
    
    @Override
    public boolean deleteAcao(String nome) {
        return deleteByNome(nome);
    }
    
    @Override
    public Acao getAcao(String nome) {
        return findByNome(nome);
    }
    
    @Override
    public boolean removeAcao(Acao acao) {
        if (acao.getId() != null) {
            return delete(acao.getId());
        }
        return false;
    }
}
