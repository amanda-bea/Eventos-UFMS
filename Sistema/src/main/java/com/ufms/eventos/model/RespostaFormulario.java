package com.ufms.eventos.model;

import java.util.Map;
import java.util.HashMap;
import lombok.Data;

@Data
public class RespostaFormulario {
    private Integer id; // Chave primária da tabela
    private String nome;
    private String email;
    private String cpf;
    private String curso;
    private Map<String, String> respostasExtras = new HashMap<>();
    private Acao acao; // Referência correta usando o objeto, em vez de 'String nomeAcao'
}