package com.ufms.eventos.services;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.EventoRepositoryJDBC;
import com.ufms.eventos.repository.AdminRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de Serviço que contém a lógica de negócio EXCLUSIVA do Administrador.
 */
public class AdminService {

    private final EventoRepository eventoRepository;
    private final AcaoRepository acaoRepository;

    /**
     * Construtor padrão
     */
    public AdminService() {
        this.eventoRepository = new EventoRepositoryJDBC();
        this.acaoRepository = new AcaoRepositoryJDBC();
    }

    /**
     * Construtor para injeção de dependência (facilita testes)
     */
    public AdminService(AdminRepository adminRepository, 
                        EventoRepository eventoRepository, 
                        AcaoRepository acaoRepository) {
        this.eventoRepository = eventoRepository;
        this.acaoRepository = acaoRepository;
    }

    /**
     * Busca todos os eventos com status "Aguardando aprovação".
     * @return Lista de DTOs com informações básicas dos eventos aguardando aprovação
     */
    public List<EventoMinDTO> listarEventosAguardando() {
        List<Evento> eventos = null;
        try {
            eventos = eventoRepository.listarTodos();
        } catch (Exception e) {
            System.err.println("Erro ao listar eventos: " + e.getMessage());
            return new ArrayList<>();
        }
        
        if (eventos == null) {
            return new ArrayList<>();
        }
        
        return eventos.stream()
                .filter(e -> "Aguardando aprovação".equalsIgnoreCase(e.getStatus()))
                .map(evento -> new EventoMinDTO(evento))
                .collect(Collectors.toList());
    }

    /**
     * Aprova um evento, atualizando seu status e o de suas ações para "Ativo".
     * @param eventoId ID do evento a ser aprovado
     * @return true se o evento foi aprovado com sucesso, false caso contrário
     */
    public boolean aprovarEvento(Long eventoId) {
        if (eventoId == null) {
            System.err.println("ID do evento não pode ser nulo");
            return false;
        }
        
        Evento evento = null;
        try {
            evento = eventoRepository.buscarPorId(eventoId);
        } catch (Exception e) {
            System.err.println("Erro ao buscar evento: " + e.getMessage());
            return false;
        }
        
        if (evento == null) {
            System.err.println("Evento com ID " + eventoId + " não encontrado");
            return false;
        }
        
        if (!"Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            System.err.println("Evento não está aguardando aprovação. Status atual: " + evento.getStatus());
            return false;
        }
        
        // Atualiza o status do evento
        evento.setStatus("Ativo");
        try {
            eventoRepository.atualizar(evento);
        } catch (Exception e) {
            System.err.println("Falha ao atualizar o status do evento: " + e.getMessage());
            return false;
        }

        System.out.println("Evento aprovado com sucesso: " + evento.getNome());

        // Aprova também todas as ações associadas
        List<Acao> acoes = acaoRepository.findByEventoId(eventoId);
        if (acoes == null || acoes.isEmpty()) {
            return true; // Evento sem ações, mas aprovação foi bem-sucedida
        }
        
        boolean todasAcoesAtualizadas = true;
        for (Acao acao : acoes) {
            acao.setStatus("Ativo");
            boolean sucessoAcao = acaoRepository.updateAcao(acao);
            
            if (sucessoAcao) {
                System.out.println("Ação aprovada: " + acao.getNome());
            } else {
                System.err.println("Falha ao atualizar o status da ação: " + acao.getNome());
                todasAcoesAtualizadas = false;
            }
        }
        
        if (!todasAcoesAtualizadas) {
            System.err.println("Algumas ações não puderam ser atualizadas");
        }
        
        return true;
    }

    /**
     * Rejeita um evento, atualizando seu status e salvando o motivo.
     * @param eventoId ID do evento a ser rejeitado
     * @param motivo Motivo da rejeição
     * @return true se o evento foi rejeitado com sucesso, false caso contrário
     */
    public boolean rejeitarEvento(Long eventoId, String motivo) {
        if (eventoId == null) {
            System.err.println("ID do evento não pode ser nulo");
            return false;
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            System.err.println("É necessário informar um motivo para rejeitar o evento");
            return false;
        }
        
        Evento evento = null;
        try {
            evento = eventoRepository.buscarPorId(eventoId);
        } catch (Exception e) {
            System.err.println("Erro ao buscar evento: " + e.getMessage());
            return false;
        }
        
        if (evento == null) {
            System.err.println("Evento com ID " + eventoId + " não encontrado");
            return false;
        }
        
        if (!"Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            System.err.println("Evento não está aguardando aprovação. Status atual: " + evento.getStatus());
            return false;
        }
        
        // Atualiza o status e motivo de rejeição
        evento.setStatus("Rejeitado");
        evento.setMensagemRejeicao(motivo);
        try {
            eventoRepository.atualizar(evento);
        } catch (Exception e) {
            System.err.println("Falha ao atualizar o status do evento: " + e.getMessage());
            return false;
        }

        System.out.println("Evento rejeitado: " + evento.getNome() + " - Motivo: " + motivo);

        // Rejeita também as ações associadas
        List<Acao> acoes = acaoRepository.findByEventoId(eventoId);
        if (acoes == null || acoes.isEmpty()) {
            return true; // Evento sem ações, mas rejeição foi bem-sucedida
        }
        
        for (Acao acao : acoes) {
            acao.setStatus("Rejeitado");
            acao.setMensagemRejeicao("O evento principal foi rejeitado. Motivo: " + motivo);
            boolean sucessoAcao = acaoRepository.updateAcao(acao);
            
            if (sucessoAcao) {
                System.out.println("Ação rejeitada: " + acao.getNome());
            } else {
                System.err.println("Falha ao atualizar o status da ação: " + acao.getNome());
            }
        }
        
        return true;
    }

    /**
     * Cancela um evento. Esta é uma ação exclusiva do administrador.
     * @param eventoNome Nome do evento a ser cancelado
     * @param motivo Motivo do cancelamento
     * @return true se o evento foi cancelado com sucesso, false caso contrário
     */
    public boolean cancelarEvento(String eventoNome, String motivo) {
        if (eventoNome == null || eventoNome.trim().isEmpty()) {
            System.err.println("Nome do evento não pode ser nulo ou vazio");
            return false;
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            System.err.println("É necessário informar um motivo para cancelar o evento");
            return false;
        }
        
        // Como não há getEvento(String) na interface, precisamos encontrar o evento pelo nome
        List<Evento> eventos = null;
        try {
            eventos = eventoRepository.listarTodos();
        } catch (Exception e) {
            System.err.println("Erro ao listar eventos: " + e.getMessage());
            return false;
        }
        
        if (eventos == null || eventos.isEmpty()) {
            System.err.println("Nenhum evento encontrado no sistema");
            return false;
        }
        
        Evento evento = eventos.stream()
                .filter(e -> eventoNome.equalsIgnoreCase(e.getNome()))
                .findFirst()
                .orElse(null);
        
        if (evento == null) {
            System.err.println("Evento '" + eventoNome + "' não encontrado");
            return false;
        }
        
        // Verifica se o evento já está cancelado
        if ("Cancelado".equalsIgnoreCase(evento.getStatus())) {
            System.err.println("Evento já está cancelado: " + eventoNome);
            return false;
        }
        
        // Atualiza o status e motivo de cancelamento
        evento.setStatus("Cancelado");
        evento.setMensagemRejeicao(motivo);
        try {
            eventoRepository.atualizar(evento);
        } catch (Exception e) {
            System.err.println("Falha ao cancelar o evento: " + e.getMessage());
            return false;
        }

        System.out.println("Evento cancelado: " + eventoNome + " - Motivo: " + motivo);

        // Cancela também as ações associadas
        List<Acao> acoes = acaoRepository.findByEventoId(evento.getId());
        if (acoes == null || acoes.isEmpty()) {
            return true; // Evento sem ações, mas cancelamento foi bem-sucedido
        }
        
        for (Acao acao : acoes) {
            acao.setStatus("Cancelado");
            acao.setMensagemRejeicao("O evento principal foi cancelado. Motivo: " + motivo);
            boolean sucessoAcao = acaoRepository.updateAcao(acao);
            
            if (sucessoAcao) {
                System.out.println("Ação cancelada: " + acao.getNome());
            } else {
                System.err.println("Falha ao cancelar a ação: " + acao.getNome());
            }
        }
        
        return true;
    }
    
    /**
     * Lista todos os eventos no sistema, independente do status.
     * @return Lista de DTOs com informações básicas de todos os eventos
     */
    public List<EventoMinDTO> listarTodosEventos() {
        List<Evento> eventos = null;
        try {
            eventos = eventoRepository.listarTodos();
        } catch (Exception e) {
            System.err.println("Erro ao listar eventos: " + e.getMessage());
            return new ArrayList<>();
        }
        
        if (eventos == null) {
            return new ArrayList<>();
        }
        
        return eventos.stream()
                .map(evento -> new EventoMinDTO(evento))
                .collect(Collectors.toList());
    }
    
    /**
     * Lista eventos com base em um status específico.
     * @param status Status desejado (Ativo, Cancelado, Rejeitado, etc.)
     * @return Lista de DTOs com informações básicas dos eventos com o status especificado
     */
    public List<EventoMinDTO> listarEventosPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Evento> eventos = null;
        try {
            eventos = eventoRepository.listarTodos();
        } catch (Exception e) {
            System.err.println("Erro ao listar eventos: " + e.getMessage());
            return new ArrayList<>();
        }
        
        if (eventos == null) {
            return new ArrayList<>();
        }
        
        return eventos.stream()
                .filter(e -> status.equalsIgnoreCase(e.getStatus()))
                .map(evento -> new EventoMinDTO(evento))
                .collect(Collectors.toList());
    }
}