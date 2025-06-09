package com.ufms.eventos.repository;

import com.ufms.eventos.model.*;
import com.ufms.eventos.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoRepositoryJDBC implements EventoRepository {

    @Override
    public void salvar(Evento evento) throws Exception {
        String sql = "INSERT INTO eventos (nome, data_inicio, data_fim, descricao, organizador_nome, departamento, categoria, imagem, link, status, mensagem_rejeicao) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setDate(2, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(3, Date.valueOf(evento.getDataFim()));
            stmt.setString(4, evento.getDescricao());
            stmt.setString(5, evento.getOrganizador().getNome());
            stmt.setString(6, evento.getDepartamento().name()); // Agora usando name() do enum
            stmt.setString(7, evento.getCategoria().name()); // Agora usando name() do enum
            stmt.setString(8, evento.getImagem());
            stmt.setString(9, evento.getLink());
            stmt.setString(10, evento.getStatus());
            stmt.setString(11, evento.getMensagemRejeicao());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                evento.setId(rs.getLong("id"));
            }
        }
    }

    @Override
    public Evento buscarPorId(Long id) throws Exception {
        String sql = "SELECT e.*, u.email, u.telefone FROM eventos e " +
                     "LEFT JOIN organizadores o ON e.organizador_nome = o.nome " +
                     "LEFT JOIN usuarios u ON o.nome = u.nome " +
                     "WHERE e.id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEvento(rs);
            }
            return null;
        }
    }

    @Override
    public List<Evento> listarTodos() throws Exception {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT e.*, u.email, u.telefone FROM eventos e " +
                     "LEFT JOIN organizadores o ON e.organizador_nome = o.nome " +
                     "LEFT JOIN usuarios u ON o.nome = u.nome";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                eventos.add(mapearEvento(rs));
            }
        }
        return eventos;
    }

    @Override
    public void atualizar(Evento evento) throws Exception {
        String sql = "UPDATE eventos SET nome = ?, data_inicio = ?, data_fim = ?, descricao = ?, organizador_nome = ?, " +
                     "departamento = ?, categoria = ?, imagem = ?, link = ?, status = ?, mensagem_rejeicao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setDate(2, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(3, Date.valueOf(evento.getDataFim()));
            stmt.setString(4, evento.getDescricao());
            stmt.setString(5, evento.getOrganizador().getNome());
            stmt.setString(6, evento.getDepartamento().name()); // Agora usando name() do enum
            stmt.setString(7, evento.getCategoria().name()); // Agora usando name() do enum
            stmt.setString(8, evento.getImagem());
            stmt.setString(9, evento.getLink());
            stmt.setString(10, evento.getStatus());
            stmt.setString(11, evento.getMensagemRejeicao());
            stmt.setLong(12, evento.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Long id) throws Exception {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
    
    public List<Evento> findByStatus(String status) throws Exception {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT e.*, u.email, u.telefone FROM eventos e " +
                     "LEFT JOIN organizadores o ON e.organizador_nome = o.nome " +
                     "LEFT JOIN usuarios u ON o.nome = u.nome " +
                     "WHERE e.status = ?";
                     
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                eventos.add(mapearEvento(rs));
            }
        }
        return eventos;
    }

    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setNome(rs.getString("nome"));
        evento.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        evento.setDataFim(rs.getDate("data_fim").toLocalDate());
        evento.setDescricao(rs.getString("descricao"));

        // Organizador agora obtém informações completas do join com usuarios
        String organizadorNome = rs.getString("organizador_nome");
        if (organizadorNome != null) {
            Organizador organizador = new Organizador();
            organizador.setNome(organizadorNome);
            
            // Dados adicionais do usuário, se disponíveis do JOIN
            String email = rs.getString("email");
            String telefone = rs.getString("telefone");
            if (email != null) {
                organizador.setEmail(email);
            }
            if (telefone != null) {
                organizador.setTelefone(telefone);
            }
            
            evento.setOrganizador(organizador);
        }

        // Departamento agora é convertido de string para enum
        String departamentoNome = rs.getString("departamento");
        if (departamentoNome != null && !departamentoNome.isEmpty()) {
            try {
                evento.setDepartamento(Departamento.valueOf(departamentoNome));
            } catch (IllegalArgumentException e) {
                System.err.println("Erro ao converter departamento: " + departamentoNome);
                // Tratamento de erro - pode definir um departamento padrão ou deixar nulo
            }
        }

        // Categoria agora é convertida de string para enum
        String categoriaNome = rs.getString("categoria");
        if (categoriaNome != null && !categoriaNome.isEmpty()) {
            try {
                evento.setCategoria(Categoria.valueOf(categoriaNome));
            } catch (IllegalArgumentException e) {
                System.err.println("Erro ao converter categoria: " + categoriaNome);
                // Tratamento de erro - pode definir uma categoria padrão ou deixar nulo
            }
        }

        evento.setImagem(rs.getString("imagem"));
        evento.setLink(rs.getString("link"));
        evento.setStatus(rs.getString("status"));
        evento.setMensagemRejeicao(rs.getString("mensagem_rejeicao"));

        return evento;
    }
}