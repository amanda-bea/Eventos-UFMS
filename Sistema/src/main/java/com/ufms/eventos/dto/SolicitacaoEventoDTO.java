package com.ufms.eventos.dto;

import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Evento;
import lombok.Data;

@Data
public class SolicitacaoEventoDTO {
    private String nome;
    private String dataInicio;
    private String dataFim;
    private String descricao;
    private Departamento departamento;
    private Categoria categoria;
    private String imagem;
    private String link;

    public SolicitacaoEventoDTO(Evento evento) {
        this.nome = evento.getNome();
        this.dataInicio = evento.getDataInicio().toString();
        this.dataFim = evento.getDataFim().toString();
        this.descricao = evento.getDescricao();
        this.departamento = evento.getDepartamento();
        this.categoria = evento.getCategoria();
        this.imagem = evento.getImagem();
        this.link = evento.getLink();
    }
}
