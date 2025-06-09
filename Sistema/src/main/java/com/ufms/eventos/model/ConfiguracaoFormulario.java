package com.ufms.eventos.model;

import java.util.List;
import java.util.ArrayList;
import lombok.Data;

@Data
public class ConfiguracaoFormulario {
    private Long id; // Chave primária da tabela
    private Acao acao; // Referência correta usando o objeto
    private boolean usarNome = true; // Valores padrão podem ser definidos aqui
    private boolean usarEmail = true;
    private boolean usarCpf = false;
    private boolean usarCurso = false;
    private List<String> camposPersonalizados = new ArrayList<>();
}