package com.ufms.eventos.repository;

import com.ufms.eventos.model.Formulario;
import java.util.HashMap;
import java.util.Map;

public class FormularioRepository {

    private Map<String, Formulario> formulariosPorAcao = new HashMap<>();

    public void salvarFormulario(String nomeAcao, Formulario formulario) {
        formulariosPorAcao.put(nomeAcao, formulario);
    }

    public Formulario buscarFormulario(String nomeAcao) {
        return formulariosPorAcao.get(nomeAcao);
    }

    public boolean existeFormulario(String nomeAcao) {
        return formulariosPorAcao.containsKey(nomeAcao);
    }

    public void deletarFormulario(String nomeAcao) {
        formulariosPorAcao.remove(nomeAcao);
    }
}
