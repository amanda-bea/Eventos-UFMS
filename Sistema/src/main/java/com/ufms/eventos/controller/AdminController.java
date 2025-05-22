package com.ufms.eventos.controller;

import com.ufms.eventos.services.AdminService;
import com.ufms.eventos.dto.EventoDTO;
import java.util.List;

public class AdminController {
    private AdminService adminService;

    public AdminController() {
        this.adminService = new AdminService();
    }

    public boolean aprovarEvento(String nomeEvento) {
        return adminService.aprovarEvento(nomeEvento);
    }

    public boolean rejeitarEvento(String nomeEvento, String mensagemRejeicao) {
        return adminService.rejeitarEvento(nomeEvento, mensagemRejeicao);
    }

    public boolean cancelarEvento(String nomeEvento, String motivo) {
        return adminService.cancelarEvento(nomeEvento, motivo);
    }

    public List<EventoDTO> listarEventosAguardando() {
        return adminService.listarEventosAguardando();
    }




}
