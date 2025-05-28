package com.ufms.eventos.controller;

import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;

import com.ufms.eventos.services.ConfiguracaoFormularioService;

import com.ufms.eventos.repository.ConfiguracaoFormularioRepository;

import java.util.Optional; // Para lidar com retornos opcionais do service

public class ConfiguracaoFormularioController {

    private final ConfiguracaoFormularioService configuracaoFormularioService;

    public ConfiguracaoFormularioController() {
        this.configuracaoFormularioService = new ConfiguracaoFormularioService(new ConfiguracaoFormularioRepository());
    }

    // Construtor para injeção de dependências (melhor prática)
    public ConfiguracaoFormularioController(ConfiguracaoFormularioService configuracaoFormularioService) {
        this.configuracaoFormularioService = configuracaoFormularioService;
    }

    public ConfiguracaoFormularioDTO buscarConfiguracao(String nomeAcao) {
        Optional<ConfiguracaoFormularioDTO> dtoOptional = configuracaoFormularioService.buscarConfiguracaoFormularioPorNomeAcao(nomeAcao);
        return dtoOptional.orElse(null); // Retorna o DTO ou null
    }

    public ConfiguracaoFormularioDTO criarConfiguracaoFormulario(ConfiguracaoFormularioDTO dto) {
        return configuracaoFormularioService.criarConfiguracaoFormulario(dto);
    }

    public boolean deletarConfiguracaoFormulario(String nomeAcao) {
        return configuracaoFormularioService.deletarConfiguracaoFormulario(nomeAcao);
    }

}