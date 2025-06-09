package com.ufms.eventos.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Adicionado para conveniência
import lombok.AllArgsConstructor; // Adicionado para conveniência
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com todos os argumentos
public class ConfiguracaoFormularioDTO {
    private String nomeAcao;
    private boolean usarNome;
    private boolean usarEmail;
    private boolean usarCpf;
    private boolean usarCurso;
    private List<String> camposPersonalizados = new ArrayList<>();
}