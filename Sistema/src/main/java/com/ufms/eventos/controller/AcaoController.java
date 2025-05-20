package com.ufms.eventos.controller;

import java.time.LocalDate;
import java.time.LocalTime;
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
    // Converte os campos do DTO para os tipos corretos
    LocalDate data = LocalDate.parse(acaoDTO.getData());
    LocalTime horarioInicio = LocalTime.parse(acaoDTO.getHorarioInicio());
    LocalTime horarioFim = LocalTime.parse(acaoDTO.getHorarioFim());

    // Define status e mensagem de rejeição iniciais
    String status = "Aguardando aprovação";
    String mensagemRejeicao = null;

    return acaoService.addAcao(
        null, // ou passe um Evento se necessário
        acaoDTO.getNome(),
        data,
        acaoDTO.getDescricao(),
        acaoDTO.getLocal(),
        horarioInicio,
        horarioFim,
        organizador,
        acaoDTO.getDepartamento(),
        acaoDTO.getContato(),
        acaoDTO.getModalidade(),
        acaoDTO.getCategoria(),
        acaoDTO.getImagem(),
        acaoDTO.getLink(),
        Integer.parseInt(acaoDTO.getCapacidade()),
        status,
        mensagemRejeicao
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
