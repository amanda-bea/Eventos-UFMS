package com.ufms.eventos.controller;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.services.AdminService; // <-- MUDANÇA IMPORTANTE: usa o AdminService

import java.util.List;

/**
 * Controller que lida com as operações exclusivas do Administrador.
 * Funciona como um ponto de entrada para a interface do admin.
 * DELEGA as chamadas para o AdminService.
 */
public class AdminController {

    private AdminService adminService; // <-- MUDANÇA: Agora usa a referência do AdminService

    public AdminController() {
        // MUDANÇA: Instancia o AdminService
        this.adminService = new AdminService();
    }

    /**
     * Busca todos os eventos com status "Aguardando aprovação".
     */
    public List<EventoMinDTO> listarEventosAguardando() {
        // MUDANÇA: Chama o método do adminService
        return adminService.listarEventosAguardando();
    }

    /**
     * Aprova um evento pendente.
     */
    public boolean aprovarEvento(String nomeEvento) {
        // MUDANÇA: Chama o método do adminService
        return adminService.aprovarEvento(nomeEvento);
    }

    /**
     * Rejeita um evento pendente, com um motivo.
     */
    public boolean rejeitarEvento(String nomeEvento, String motivo) {
        // MUDANÇA: Chama o método do adminService
        return adminService.rejeitarEvento(nomeEvento, motivo);
    }

    /**
     * Cancela um evento.
     */
    public boolean cancelarEvento(String nomeEvento, String motivo) {
        // MUDANÇA: Chama o método do adminService
        return adminService.cancelarEvento(nomeEvento, motivo);
    }
}