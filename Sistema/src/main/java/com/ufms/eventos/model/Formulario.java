package com.ufms.eventos.model;

import java.util.Map;
import lombok.Data;

@Data
public class Formulario {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private Map<String, String> camposExtras;

    public Formulario(){}
}
