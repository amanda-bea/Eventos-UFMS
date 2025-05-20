package com.ufms.eventos.services;

import com.ufms.eventos.dto.FormularioDTO;
import com.ufms.eventos.model.Formulario;
import com.ufms.eventos.repository.FormularioRepository;

import java.util.Map;

public class FormularioService {

    private FormularioRepository formularioRepository;

    public FormularioService() {
        this.formularioRepository = new FormularioRepository();
    }

    // Salva um formulário a partir do DTO
    //public FormularioDTO salvarFormulario(FormularioDTO dto) {
    //    Formulario formulario = new Formulario(dto);
    //    Formulario salvo = formularioRepository.addFormulario(formulario);
    //    return new FormularioDTO(salvo);
    //}

    // Lista todos os formulários como DTOs NAO SEI PQ N FUNCIONA
    //public List<FormularioDTO> listarFormularios() {
    //    return formularioRepository.getFormularios()
    //            .stream()
    //            .map(FormularioDTO::new)
    //            .collect(Collectors.toList());
    //}

    // Adiciona campo extra ao formulário (por DTO)
    public boolean adicionarCampoExtra(FormularioDTO formularioDTO, String campo, String valor) {
        Formulario formulario = formularioRepository.getFormulario(formularioDTO.getId());
        if (formulario.getCamposExtras().containsKey(campo)) {
            return false;
        }
        formulario.getCamposExtras().put(campo, valor);
        formularioRepository.updateFormulario(formulario.getId(), formulario);
        return true;
    }

    // Remove campo extra do formulário (por DTO)
    public boolean removerCampoExtra(FormularioDTO formularioDTO, String campo) {
        Formulario formulario = formularioRepository.getFormulario(formularioDTO.getId());
        if (formulario == null) {
            return false;
        }
        if (!formulario.getCamposExtras().containsKey(campo)) {
            return false;
        }
        formulario.getCamposExtras().remove(campo);
        formularioRepository.updateFormulario(formulario.getId(), formulario);
        return true;
    }

    // Exibe campos do formulário (por DTO)
    public void exibirCampos(FormularioDTO formulario) {
        System.out.println("Campos padrão:");
        System.out.println("Nome: " + formulario.getNome());
        System.out.println("Email: " + formulario.getEmail());
        System.out.println("Telefone: " + formulario.getTelefone());
        System.out.println("CPF: " + formulario.getCpf());

        System.out.println("Campos extras:");
        for (Map.Entry<String, String> entry : formulario.getCamposExtras().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public boolean validarFormulario(FormularioDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            return false;
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            return false;
        }
        // Outras validações
        return true;
    }
}