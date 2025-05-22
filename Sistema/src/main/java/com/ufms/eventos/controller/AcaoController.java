package com.ufms.eventos.controller;

import java.util.HashSet;

import com.ufms.eventos.model.Organizador;

import com.ufms.eventos.services.AcaoService;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarAcaoDTO;
import com.ufms.eventos.dto.EventoDTO;


public class AcaoController {
    private AcaoService acaoService;

    public AcaoController() {
        this.acaoService = new AcaoService();
    }

    public boolean solicitarAcao(AcaoDTO acaoDTO, Organizador organizador, EventoDTO eventoDTO) {
        return acaoService.solicitarAcao(acaoDTO, organizador, eventoDTO);
    }

    public HashSet<AcaoDTO> listarAcoes() {
        return acaoService.listarAcoes();
    }

    public boolean editarAcao(EditarAcaoDTO acao, Organizador organizador) {
        return acaoService.editarAcao(acao, organizador);
    }

    public boolean deletarAcao(String nome) {
        return acaoService.deletarAcao(nome);
    }

    public AcaoDTO getAcao(String nome) {
        return acaoService.getAcao(nome);
    }

    public boolean adicionarAcaoEmEventoExistente(String nomeEvento, AcaoDTO acaoDTO, Organizador organizador) {
        return acaoService.adicionarAcaoEmEventoExistente(nomeEvento, acaoDTO, organizador);
    }

    

}
