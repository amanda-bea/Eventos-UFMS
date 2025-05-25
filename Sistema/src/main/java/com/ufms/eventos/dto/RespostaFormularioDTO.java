package com.ufms.eventos.dto;

import java.util.Map;

import lombok.Data;

@Data
public class RespostaFormularioDTO {
    private String nome;
    private String email;
    private String cpf;
    private String curso;
    // Campos extras adicionados dinamicamente pelo organizador
    private Map<String, String> respostasExtras; // chave = nome do campo, valor = resposta
    private String acaoNome;

}
