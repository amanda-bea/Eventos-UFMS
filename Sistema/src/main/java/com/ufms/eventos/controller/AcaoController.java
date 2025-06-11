package com.ufms.eventos.controller;

import com.ufms.eventos.model.Usuario; // <-- Importe o Usuario
import com.ufms.eventos.services.AcaoService;
import com.ufms.eventos.dto.AcaoDTO; // Adicionei este import, pois é útil
import com.ufms.eventos.dto.EditarAcaoDTO;

import java.util.List;

/**
 * Controller que lida com as operações relacionadas a Ações de eventos.
 * Funciona como um ponto de entrada para a UI, delegando a lógica para o AcaoService.
 */
public class AcaoController {
    private AcaoService acaoService;

    public AcaoController() {
        this.acaoService = new AcaoService();
    }

    /**
     * Adiciona uma nova ação a um evento existente.
     * a ser implementado
     */
    public boolean adicionarAcaoEmEventoExistente(Long eventoId, AcaoDTO acaoDTO, Usuario usuarioLogado) {
        return acaoService.adicionarAcaoEmEventoExistente(eventoId, acaoDTO, usuarioLogado);
    }

    // a ser implementado
    public boolean editarAcao(EditarAcaoDTO dto, Usuario usuarioLogado) {
        return acaoService.editarAcao(dto, usuarioLogado);
    }

    public boolean deletarAcao(String nomeAcao, Usuario usuarioLogado) {
        return acaoService.deletarAcao(nomeAcao, usuarioLogado);
    }

    public List<AcaoDTO> listarAcoesPorEventoComAvisos(Long eventoId) {
        return acaoService.listarAcoesPorEventoComAvisos(eventoId);
    }

    public List<AcaoDTO> listarAcoesCompletasPorEvento(Long eventoId) {
        return acaoService.listarAcoesCompletasPorEvento(eventoId);
    }

}