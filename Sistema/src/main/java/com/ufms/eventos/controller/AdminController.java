package com.ufms.eventos.controller;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.services.AdminService;

import java.util.List;

/**
 * Controller que lida com as operações exclusivas do Administrador.
 * Funciona como um ponto de entrada para a interface do admin.
 * DELEGA as chamadas para o AdminService.
 */
public class AdminController {

    private AdminService adminService;

    /**
     * Construtor padrão que inicializa o serviço de administração.
     */
    public AdminController() {
        this.adminService = new AdminService();
    }
    
    /**
     * Construtor para injeção de dependência (facilita testes).
     * @param adminService O serviço de administração a ser utilizado.
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Busca todos os eventos com status "Aguardando aprovação".
     * @return Lista de DTOs com informações básicas dos eventos aguardando aprovação.
     */
    public List<EventoMinDTO> listarEventosAguardando() {
        return adminService.listarEventosAguardando();
    }

    /**
     * Aprova um evento, atualizando seu status e o de suas ações para "Ativo".
     * @param eventoId ID do evento a ser aprovado.
     * @return true se o evento foi aprovado com sucesso, false caso contrário.
     */
    public boolean aprovarEvento(Long eventoId) {
        return adminService.aprovarEvento(eventoId);
    }

    /**
     * Rejeita um evento, atualizando seu status e salvando o motivo.
     * @param eventoId ID do evento a ser rejeitado.
     * @param motivo Motivo da rejeição.
     * @return true se o evento foi rejeitado com sucesso, false caso contrário.
     */
    public boolean rejeitarEvento(Long eventoId, String motivo) {
        return adminService.rejeitarEvento(eventoId, motivo);
    }

    /**
     * Cancela um evento.
     * @param nomeEvento Nome do evento a ser cancelado.
     * @param motivo Motivo do cancelamento.
     * @return true se o evento foi cancelado com sucesso, false caso contrário.
     */
    public boolean cancelarEvento(String nomeEvento, String motivo) {
        return adminService.cancelarEvento(nomeEvento, motivo);
    }
    
    /**
     * Lista todos os eventos no sistema, independente do status.
     * @return Lista de DTOs com informações básicas de todos os eventos.
     */
    public List<EventoMinDTO> listarTodosEventos() {
        return adminService.listarTodosEventos();
    }
    
    /**
     * Lista eventos com base em um status específico.
     * @param status Status desejado (Ativo, Cancelado, Rejeitado, etc.).
     * @return Lista de DTOs com informações básicas dos eventos com o status especificado.
     */
    public List<EventoMinDTO> listarEventosPorStatus(String status) {
        return adminService.listarEventosPorStatus(status);
    }
}