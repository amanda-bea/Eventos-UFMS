package com.ufms.eventos.controller;

import com.ufms.eventos.services.AdminService;
import com.ufms.eventos.dto.EventoMinDTO;

import java.util.List;

public class AdminController {
    private AdminService adminService;

    public AdminController() {
        this.adminService = new AdminService();
    }

    public List<EventoMinDTO> listarEventosAguardando() {
        return adminService.listarEventosAguardando();
    }

    public boolean aprovarEvento(String nomeEvento) {
        return adminService.aprovarEvento(nomeEvento);
    }

    public boolean rejeitarEvento(String nomeEvento, String motivo) {
        return adminService.rejeitarEvento(nomeEvento, motivo);
    }

    public boolean cancelarEvento(String nomeEvento, String motivo) {
        return adminService.cancelarEvento(nomeEvento, motivo);
    }
}