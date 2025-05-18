package com.ufms.eventos.controller;

import java.util.HashSet;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.services.AcaoService;
import com.ufms.eventos.dto.AcaoDTO;

public class AcaoController {
    private AcaoService acaoService;
    private HashSet<AcaoDTO> acoes;

    public AcaoController() {
    this.acaoService = new AcaoService();
    this.acoes = new HashSet<>();
    }

    public boolean addAcao(AcaoDTO acaoDTO, Organizador organizador) {
    return acaoService.addAcao(
        acaoDTO.getNome(),
        acaoDTO.getData(),
        acaoDTO.getDescricao(),
        acaoDTO.getLocal(),
        acaoDTO.getHorarioInicio(),
        acaoDTO.getHorarioFim(),
        organizador, // passa o objeto organizador
        acaoDTO.getDepartamento(),
        acaoDTO.getContato(),
        acaoDTO.getModalidade(),
        acaoDTO.getCategoria(),
        acaoDTO.getImagem(),
        acaoDTO.getLink(),
        Integer.parseInt(acaoDTO.getCapacidade())
    );
    }

    public HashSet<Acao> getAcoes() {
        return acaoService.getAcoes();
    }

    public boolean updateAcao(Acao acao) {
        return acaoService.updateAcao(acao);
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

}
