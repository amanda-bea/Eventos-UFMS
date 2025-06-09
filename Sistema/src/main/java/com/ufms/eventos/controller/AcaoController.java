package com.ufms.eventos.controller;

import com.ufms.eventos.model.Usuario; // <-- Importe o Usuario
import com.ufms.eventos.services.AcaoService;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.AcaoMinDTO; // Adicionei este import, pois é útil
import com.ufms.eventos.dto.EditarAcaoDTO;

import java.util.ArrayList;
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
     * A assinatura agora recebe o 'Usuario' logado para verificação de permissão.
     */
    public boolean adicionarAcaoEmEventoExistente(Long eventoId, AcaoDTO acaoDTO, Usuario usuarioLogado) {
        return acaoService.adicionarAcaoEmEventoExistente(eventoId, acaoDTO, usuarioLogado);
    }

    /**
     * Edita uma ação existente.
     * A assinatura agora recebe o 'Usuario' logado para verificação de permissão.
     */
    public boolean editarAcao(EditarAcaoDTO dto, Usuario usuarioLogado) {
        return acaoService.editarAcao(dto, usuarioLogado);
    }

    /**
     * Deleta uma ação existente.
     * A assinatura agora recebe o 'Usuario' logado para verificação de permissão.
     */
    public boolean deletarAcao(String nomeAcao, Usuario usuarioLogado) {
        return acaoService.deletarAcao(nomeAcao, usuarioLogado);
    }
    
    /**
     * Busca os detalhes de uma única ação pelo nome.
     * Alinhado com o método corrigido no AcaoService.
     */
    public AcaoDTO getAcaoDtoPorNome(String nome) {
        return acaoService.getAcaoDtoPorNome(nome);
    }

    public List<AcaoMinDTO> listarAcoesPorEventoMin(Long eventoId) {
        return acaoService.listarAcoesPorEventoMin(eventoId);
    }

    public List<AcaoDTO> listarAcoesPorEventoComAvisos(Long eventoId) {
        // Adicione log para verificar o valor
        System.out.println("AcaoController.listarAcoesPorEventoComAvisos recebeu ID: " + eventoId);
        
        if (eventoId == null) {
            System.err.println("AVISO: AcaoController recebeu eventoId nulo");
            return new ArrayList<>();
        }
        
        return acaoService.listarAcoesPorEventoComAvisos(eventoId);
    }

    public List<AcaoDTO> listarAcoesCompletasPorEvento(Long eventoId) {
        return acaoService.listarAcoesCompletasPorEvento(eventoId);
    }

}