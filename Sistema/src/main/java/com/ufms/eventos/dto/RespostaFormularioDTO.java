package com.ufms.eventos.dto;

import java.util.Map;
import lombok.Data;

@Data
public class RespostaFormularioDTO {
    private Long acaoId; // Essencial: ID da Ação à qual esta resposta pertence
    private String nome;
    private String email;
    private String cpf;
    private String curso;
    private Map<String, String> respostasExtras;
}