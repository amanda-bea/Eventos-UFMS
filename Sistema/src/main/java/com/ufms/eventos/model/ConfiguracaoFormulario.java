package com.ufms.eventos.model;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class ConfiguracaoFormulario {
    private String nomeAcao; // A qual ação esta configuração pertence
    private boolean usarNome;
    private boolean usarEmail;
    private boolean usarCpf;
    private boolean usarCurso;
    private List<String> nomesCamposPersonalizados = new ArrayList<>(); // Nomes dos campos extras
}