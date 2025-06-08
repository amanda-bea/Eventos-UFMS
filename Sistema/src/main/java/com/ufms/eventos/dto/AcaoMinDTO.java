package com.ufms.eventos.dto;

import java.time.LocalDate;
import lombok.Data;

import com.ufms.eventos.model.Acao;

@Data
public class AcaoMinDTO {
    private Long id;
    private String nome;
    private String data;
    private String departamento;
    private String avisoVagas;

    public AcaoMinDTO(String nome, String data, String departamento, String imagem) {
        this.nome = nome;
        this.data = data;
        this.departamento = departamento;
    }

    public AcaoMinDTO(String nome, LocalDate data, String departamento, String imagem) {
        this.nome = nome;
        this.data = data.toString();
        this.departamento = departamento;
    }

    public AcaoMinDTO(AcaoDTO acao) {
        this.nome = acao.getNome();
        this.data = acao.getData();
        this.departamento = acao.getDepartamento();
    }

    public AcaoMinDTO(Acao acao) {
        this.nome = acao.getNome();
        this.data = acao.getData().toString();
        this.departamento = acao.getDepartamento().name();
    }

    public AcaoMinDTO() {
        // Construtor padrão
    }

}
