package com.ufms.eventos.dto;

import java.time.LocalDate;
import lombok.Data;

import com.ufms.eventos.model.Acao;

@Data
public class AcaoMinDTO {
    private Integer id;
    private String nome;
    private String data;
    private String departamento;
    private String imagem;

    public AcaoMinDTO(String nome, String data, String departamento, String imagem) {
        this.nome = nome;
        this.data = data;
        this.departamento = departamento;
        this.imagem = imagem;
    }

    public AcaoMinDTO(String nome, LocalDate data, String departamento, String imagem) {
        this.nome = nome;
        this.data = data.toString();
        this.departamento = departamento;
        this.imagem = imagem;
    }

    public AcaoMinDTO(AcaoDTO acao) {
        this.nome = acao.getNome();
        this.data = acao.getData();
        this.departamento = acao.getDepartamento();
        this.imagem = acao.getImagem();
    }

    public AcaoMinDTO(Acao acao) {
        this.nome = acao.getNome();
        this.data = acao.getData().toString();
        this.departamento = acao.getDepartamento().name();
        this.imagem = acao.getImagem();
    }

    public AcaoMinDTO() {
        // Construtor padrão
    }

    public boolean validate(){
        // Implementar validação dos campos
        if (nome == null || nome.isEmpty()) {
            return false;
        }
        if (data == null || data.isEmpty()) {
            return false;
        }
        if (departamento == null || departamento.isEmpty()) {
            return false;
        }
        if (imagem == null || imagem.isEmpty()) {
            return false;
        }
        return true;
    }
}
