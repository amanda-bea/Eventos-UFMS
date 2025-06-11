package com.ufms.eventos.dto;

import com.ufms.eventos.model.ConfiguracaoFormulario;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ConfiguracaoFormularioDTO {
    private Long acaoId;
    private boolean usarNome;
    private boolean usarEmail;
    private boolean usarCpf;
    private boolean usarCurso;
    private List<String> camposPersonalizados;

    public ConfiguracaoFormularioDTO(ConfiguracaoFormulario model) {
        this.acaoId = model.getAcao().getId();
        this.usarNome = model.isUsarNome();
        this.usarEmail = model.isUsarEmail();
        this.usarCpf = model.isUsarCpf();
        this.usarCurso = model.isUsarCurso();
        this.camposPersonalizados = new ArrayList<>(model.getCamposPersonalizados());
    }

    public ConfiguracaoFormularioDTO(Long acaoId, boolean usarNome, boolean usarEmail, boolean usarCpf, boolean usarCurso, List<String> camposPersonalizados) {
        this.acaoId = acaoId;
        this.usarNome = usarNome;
        this.usarEmail = usarEmail;
        this.usarCpf = usarCpf;
        this.usarCurso = usarCurso;
        this.camposPersonalizados = camposPersonalizados;
    }
}