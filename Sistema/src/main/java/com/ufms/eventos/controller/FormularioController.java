package com.ufms.eventos.controller;

import com.ufms.eventos.dto.CampoFormularioDTO;
import com.ufms.eventos.dto.FormularioDTO;
import com.ufms.eventos.model.CampoFormulario;
import com.ufms.eventos.model.Formulario;
import com.ufms.eventos.services.FormularioService;

import java.util.List;

public class FormularioController {

    private FormularioService service = new FormularioService();

    public void adicionarCampoExtra(String nomeAcao, CampoFormularioDTO campoDTO) {
        service.adicionarCampoExtra(nomeAcao, campoDTO);
    }

    public List<CampoFormulario> listarCamposExtras(String nomeAcao) {
        return service.listarCamposExtras(nomeAcao);
    }

    public boolean enviarFormulario(FormularioDTO dto) {
        return service.enviarFormulario(dto);
    }

    public List<Formulario> listarFormularios(String nomeAcao) {
        return service.listarFormulariosDaAcao(nomeAcao);
    }
}
