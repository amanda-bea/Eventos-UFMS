package com.ufms.eventos.services;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.EventoRepositoryJDBC;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminService {

    private final EventoRepository eventoRepository;
    private final AcaoRepository acaoRepository;

    public AdminService() {
        this.eventoRepository = new EventoRepositoryJDBC();
        this.acaoRepository = new AcaoRepositoryJDBC();
    }

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
            return true;
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
            return true;
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
            return true;
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