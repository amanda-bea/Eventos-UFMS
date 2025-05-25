package com.ufms.eventos.model;

import java.util.Map;
import lombok.Data;

@Data
public class RespostaFormulario {
    private String nome;
    private String email;
    private String cpf;
    private String curso;
    private Map<String, String> respostasExtras;
    private String acaoNome; // identificação da ação relacionada
}
