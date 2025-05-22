package com.ufms.eventos.dto;

import lombok.Data;

@Data
public class EditarAcaoDTO {
    private String nomeAcao;
    private String novaData;
    private String novoLocal;
    private String novoHorarioInicio;
    private String novoHorarioFim;
    private String novoLink;
    private String novaCapacidade;

    public EditarAcaoDTO(String nomeAcao, String novaData, String novoLocal, String novoHorarioInicio, String novoHorarioFim, String novoLink, String novaCapacidade) {
        this.nomeAcao = nomeAcao;
        this.novaData = novaData;
        this.novoLocal = novoLocal;
        this.novoHorarioInicio = novoHorarioInicio;
        this.novoHorarioFim = novoHorarioFim;
        this.novoLink = novoLink;
        this.novaCapacidade = novaCapacidade;
    }

    public EditarAcaoDTO() {
    }

    public EditarAcaoDTO(AcaoDTO acaoDTO) {
        this.nomeAcao = acaoDTO.getNome();
        this.novaData = acaoDTO.getData();
        this.novoLocal = acaoDTO.getLocal();
        this.novoHorarioInicio = acaoDTO.getHorarioInicio();
        this.novoHorarioFim = acaoDTO.getHorarioFim();
        this.novoLink = acaoDTO.getLink();
        this.novaCapacidade = acaoDTO.getCapacidade();
    }
}
