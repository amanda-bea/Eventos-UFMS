package com.ufms.eventos.controller;

import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.dto.PresencaConfirmadaDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.PresencaConfirmadaService;
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
    
    /**
     * Confirma a presença de um usuário em uma ação.
     */
    public boolean confirmarPresenca(PresencaConfirmadaDTO dto) {
        return service.confirmarPresenca(dto);
    }
    
    /**
     * Cancela a presença de um usuário em uma ação.
     */
    public boolean cancelarPresenca(String nomeUsuario, String nomeAcao) {
        return service.cancelarPresenca(nomeUsuario, nomeAcao);
    }
    
    /**
     * Conta o número de presenças confirmadas para uma ação.
     */
    public int contarPresencasConfirmadas(Long idAcao) {
        return service.contarPresencasConfirmadas(idAcao);
    }
}