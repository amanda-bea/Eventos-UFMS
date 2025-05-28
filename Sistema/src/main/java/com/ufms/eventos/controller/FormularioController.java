package com.ufms.eventos.controller;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.CampoFormularioDTO;
import com.ufms.eventos.dto.FormularioDTO;
import com.ufms.eventos.dto.PresencaConfirmadaDTO;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.CampoFormulario;
import com.ufms.eventos.model.Formulario;
import com.ufms.eventos.model.Usuario;

import com.ufms.eventos.services.FormularioService;
import com.ufms.eventos.services.PresencaConfirmadaService;

import com.ufms.eventos.services.UsuarioService;
import com.ufms.eventos.services.AcaoService;

import java.util.List;

public class FormularioController {

    private FormularioService formularioService;
    private PresencaConfirmadaService presencaConfirmadaService;
    private UsuarioService usuarioService;
    private AcaoService acaoService;

    public FormularioController() {
        this.formularioService = new FormularioService();
        this.presencaConfirmadaService = new PresencaConfirmadaService();
        this.usuarioService = new UsuarioService();
        this.acaoService = new AcaoService();
    }

    public void adicionarCampoExtra(String nomeAcao, CampoFormularioDTO campoDTO) {
        formularioService.adicionarCampoExtra(nomeAcao, campoDTO);
    }

    public List<CampoFormulario> listarCamposExtras(String nomeAcao) {
        return formularioService.listarCamposExtras(nomeAcao);
    }

    public boolean enviarFormulario(FormularioDTO dto) {
        return formularioService.enviarFormulario(dto);
    }

    public List<Formulario> listarFormularios(String nomeAcao) {
        return formularioService.listarFormulariosDaAcao(nomeAcao);
    }


    public boolean confirmarPresencaParaAcao(PresencaConfirmadaDTO dto) {
        if (dto == null || dto.getUsuario() == null || dto.getUsuario().trim().isEmpty() ||
            dto.getAcao() == null || dto.getAcao().trim().isEmpty()) {
            System.err.println("FormularioController (confirmarPresencaParaAcao): DTO de confirmação ou seus campos essenciais são nulos/vazios.");
            return false;
        }

        // 1. Buscar a entidade Usuario usando o nome do DTO
        Usuario usuario = usuarioService.buscarPorNome(dto.getUsuario());
        if (usuario == null) {
            System.err.println("FormularioController (confirmarPresencaParaAcao): Usuário não encontrado com o nome: " + dto.getUsuario());
            return false;
        }

        // 2. Buscar a entidade Acao usando o nome do DTO (retornando o model, não o DTO)
        Acao acao = acaoService.buscarPorNome(dto.getAcao()); // <-- troque para o método correto!
        if (acao == null) {
            System.err.println("FormularioController (confirmarPresencaParaAcao): Ação não encontrada com o nome: " + dto.getAcao());
            return false;
        }

        // 3. Chamar o serviço de confirmação de presença com as entidades recuperadas
        return presencaConfirmadaService.confirmarPresenca(dto);
    }
}