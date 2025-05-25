package com.ufms.eventos.controller;

import java.util.HashSet;

import com.ufms.eventos.model.Organizador;

import com.ufms.eventos.services.AcaoService;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarAcaoDTO;
import com.ufms.eventos.dto.EventoDTO;

import com.ufms.eventos.ui.LoginFXMLController;


public class AcaoController {
    private AcaoService acaoService;

    public AcaoController() {
        this.acaoService = new AcaoService();
    }

    public boolean solicitarAcao(AcaoDTO acaoDTO, LoginFXMLController loginController, EventoDTO eventoDTO) {
        Organizador organizador = (Organizador) loginController.getUsuarioLogado();
        return acaoService.solicitarAcao(acaoDTO, organizador, eventoDTO);
    }

    public HashSet<AcaoDTO> listarAcoes() {
        return acaoService.listarAcoes();
    }

    public boolean editarAcao(EditarAcaoDTO acao, LoginFXMLController loginController) {
        Organizador organizador = (Organizador) loginController.getUsuarioLogado();
        return acaoService.editarAcao(acao, organizador);
    }

    public boolean deletarAcao(String nome) {
        return acaoService.deletarAcao(nome);
    }

    public AcaoDTO getAcao(String nome) {
        return acaoService.getAcao(nome);
    }

    public boolean adicionarAcaoEmEventoExistente(String nomeEvento, AcaoDTO acaoDTO, LoginFXMLController loginController) {
        Organizador organizador = (Organizador) loginController.getUsuarioLogado();
        return acaoService.adicionarAcaoEmEventoExistente(nomeEvento, acaoDTO, organizador);
    }

    

}
