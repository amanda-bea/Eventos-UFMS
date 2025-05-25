package com.ufms.eventos.services;

import com.ufms.eventos.dto.CampoFormularioDTO;
import com.ufms.eventos.dto.FormularioDTO;
import com.ufms.eventos.model.CampoFormulario;
import com.ufms.eventos.model.Formulario;

import java.util.*;

public class FormularioService {

    private Map<String, List<CampoFormulario>> camposExtrasPorAcao = new HashMap<>();
    private Map<String, List<Formulario>> formulariosPorAcao = new HashMap<>();

    public void adicionarCampoExtra(String nomeAcao, CampoFormularioDTO campoDTO) {
        CampoFormulario campo = new CampoFormulario();
        campo.setNomeCampo(campoDTO.getNomeCampo());

        camposExtrasPorAcao.computeIfAbsent(nomeAcao, k -> new ArrayList<>()).add(campo);
    }

    public List<CampoFormulario> listarCamposExtras(String nomeAcao) {
        return camposExtrasPorAcao.getOrDefault(nomeAcao, new ArrayList<>());
    }

    public boolean enviarFormulario(FormularioDTO dto) {
        Formulario f = new Formulario();
        f.setNomeAcao(dto.getNomeAcao());
        f.setNome(dto.getNome());
        f.setEmail(dto.getEmail());
        f.setCpf(dto.getCpf());
        f.setCurso(dto.getCurso());
        f.setRespostasExtras(dto.getRespostasExtras());

        formulariosPorAcao.computeIfAbsent(dto.getNomeAcao(), k -> new ArrayList<>()).add(f);
        return true;
    }

    public List<Formulario> listarFormulariosDaAcao(String nomeAcao) {
        return formulariosPorAcao.getOrDefault(nomeAcao, new ArrayList<>());
    }
}
