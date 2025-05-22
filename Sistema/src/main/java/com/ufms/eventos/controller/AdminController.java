package com.ufms.eventos.controller;

import com.ufms.eventos.services.AdminService;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.SolicitacaoEventoDTO;

import java.util.List;

public class AdminController {
    private AdminService adminService;

    public AdminController() {
        this.adminService = new AdminService();
    }

    public boolean aprovarEvento(SolicitacaoEventoDTO solicitacaoEventoDTO) {
        return adminService.aprovarEvento(solicitacaoEventoDTO);
    }

    public boolean rejeitarEvento(SolicitacaoEventoDTO solicitacaoEventoDTO, String mensagemRejeicao) {
        return adminService.rejeitarEvento(solicitacaoEventoDTO, mensagemRejeicao);
    }

    public boolean cancelarEvento(String nomeEvento, String motivo) {
        return adminService.cancelarEvento(nomeEvento, motivo);
    }

    public List<EventoDTO> listarEventosAguardando() {
        return adminService.listarEventosAguardando();
    }




}
