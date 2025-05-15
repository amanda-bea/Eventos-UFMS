package com.ufms.eventos.services;

import com.ufms.eventos.dto.FormularioDTO;
import com.ufms.eventos.model.Formulario;
import com.ufms.eventos.repository.FormularioRepository;

import java.util.List;

public class FormularioService {

    private final FormularioRepository repository = new FormularioRepository();

    public Formulario salvarFormulario(FormularioDTO dto) {
        Formulario formulario = new Formulario();
        formulario.setNome(dto.getNome());
        formulario.setEmail(dto.getEmail());
        formulario.setTelefone(dto.getTelefone());
        formulario.setCpf(dto.getCpf());
        formulario.setCamposExtras(dto.getCamposExtras());

        return repository.salvar(formulario);
    }

    public List<Formulario> listarFormularios() {
        return repository.listarTodas();
    }
}
