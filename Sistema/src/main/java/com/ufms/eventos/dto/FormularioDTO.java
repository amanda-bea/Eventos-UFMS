package com.ufms.eventos.dto;

import com.ufms.eventos.model.Formulario;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class FormularioDTO {
    // Atributos padrão
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private Map<String, String> camposExtras = new HashMap<>(); // Campos dinâmicos do formulário

    public FormularioDTO(Long id, String nome, String email, String telefone, String cpf) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    public FormularioDTO() {
        // Construtor padrão
    }

    public FormularioDTO(FormularioDTO dto) {
        this.id = dto.id;
        this.nome = dto.nome;
        this.email = dto.email;
        this.telefone = dto.telefone;
        this.cpf = dto.cpf;
        this.camposExtras = new HashMap<>(dto.camposExtras);
    }

    public FormularioDTO(Formulario formulario) {
        this.id = formulario.getId();
        this.nome = formulario.getNome();
        this.email = formulario.getEmail();
        this.telefone = formulario.getTelefone();
        this.cpf = formulario.getCpf();
        this.camposExtras = new HashMap<>(formulario.getCamposExtras());
    }

}