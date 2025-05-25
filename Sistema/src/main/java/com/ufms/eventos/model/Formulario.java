package com.ufms.eventos.model;

import lombok.Data;
import java.util.Map;

@Data
public class Formulario {
    private String nomeAcao;
    private String nome;
    private String email;
    private String cpf;
    private String curso;
    private Map<String, String> respostasExtras; // campo → resposta
}
