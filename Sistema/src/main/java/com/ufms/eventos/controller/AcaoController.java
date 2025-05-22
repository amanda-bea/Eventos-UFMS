package com.ufms.eventos.controller;

import java.util.HashSet;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.services.AcaoService;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarAcaoDTO;
import com.ufms.eventos.dto.EventoDTO;


public class AcaoController {
    private AcaoService acaoService;
    private HashSet<AcaoDTO> acoes;

    public AcaoController() {
        this.acaoService = new AcaoService();
        this.acoes = new HashSet<>();
    }

    public boolean addAcao(AcaoDTO acaoDTO, Organizador organizador, EventoDTO evento) {
        return acaoService.addAcao(acaoDTO, organizador, evento);
    }

    public HashSet<Acao> getAcoes() {
        return acaoService.getAcoes();
    }

    public boolean editarAcao(EditarAcaoDTO acao, Organizador organizador) {
        return acaoService.editarAcao(acao, organizador);
    }

    public boolean deleteAcao(String nome) {
        return acaoService.deleteAcao(nome);
    }

    public Acao getAcao(String nome) {
        return acaoService.getAcao(nome);
    }

    public HashSet<AcaoDTO> getAcoesDTO() {
        return acoes;
    }

    public boolean adicionarAcaoEmEventoExistente(String nomeEvento, AcaoDTO acaoDTO, Organizador organizador) {
        return acaoService.adicionarAcaoEmEventoExistente(nomeEvento, acaoDTO, organizador);
    }

    

}
