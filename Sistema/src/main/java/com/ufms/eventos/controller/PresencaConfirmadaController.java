package com.ufms.eventos.controller;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.dto.PresencaConfirmadaDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.PresencaConfirmadaService;

import java.util.ArrayList;
import java.util.List;

public class PresencaConfirmadaController {
    private PresencaConfirmadaService service;

    public PresencaConfirmadaController() {
        this.service = new PresencaConfirmadaService();
    }

    /**
     * Lista os eventos em que o usuário confirmou presença.
     */
    public List<EventoMinDTO> listarEventosComPresencaConfirmada(Usuario usuario) {
        return service.listarEventosComPresencaConfirmada(usuario);
    }
    
    public boolean confirmarPresenca(PresencaConfirmadaDTO dto) {
        return service.confirmarPresenca(dto);
    }
    
    public boolean isUsuarioInscrito(Usuario usuario, Long acaoId) {
        return service.isUsuarioInscrito(usuario, acaoId);
    }

    public boolean cancelarInscricao(Usuario usuario, Long acaoId) {
        return service.cancelarInscricao(usuario, acaoId);
    }
    
    /**
     * Conta o número de presenças confirmadas para uma ação.
     */
    public int contarPresencasConfirmadas(Long idAcao) {
        return service.contarPresencasConfirmadas(idAcao);
    }

    public List<Usuario> listarInscritosPorAcao(Long idAcao) {
        if (idAcao == null) {
            return new ArrayList<>();
        }
        return service.listarInscritosPorAcao(idAcao);
    }
}