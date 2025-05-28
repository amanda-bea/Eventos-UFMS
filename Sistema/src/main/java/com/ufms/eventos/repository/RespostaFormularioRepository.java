package com.ufms.eventos.repository;

import java.util.ArrayList;
import java.util.List;

import com.ufms.eventos.model.RespostaFormulario;

public class RespostaFormularioRepository {
    private List<RespostaFormulario> respostas = new ArrayList<>();

    public void salvar(RespostaFormulario resposta) {
        respostas.add(resposta);
    }

    public List<RespostaFormulario> listarPorAcao(String acaoNome) {
        List<RespostaFormulario> resultado = new ArrayList<>();
        for (RespostaFormulario r : respostas) {
            if (r.getNomeAcao().equalsIgnoreCase(acaoNome)) {
                resultado.add(r);
            }
        }
        return resultado;
    }
}
