package com.ufms.eventos.controller;

import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.services.ConfiguracaoFormularioService;

import java.util.Optional; // Para lidar com retornos opcionais do service

public class ConfiguracaoFormularioController {

    private final ConfiguracaoFormularioService configuracaoFormularioService;

    public ConfiguracaoFormularioController() {
        this.configuracaoFormularioService = new ConfiguracaoFormularioService();
    }

    public ConfiguracaoFormularioDTO buscarConfiguracao(String nomeAcao) {
        Optional<ConfiguracaoFormularioDTO> dtoOptional = configuracaoFormularioService.buscarConfiguracaoFormularioPorNomeAcao(nomeAcao);
        return dtoOptional.orElse(null);
    }

    public ConfiguracaoFormularioDTO criarConfiguracaoFormulario(ConfiguracaoFormularioDTO dto) {
        return configuracaoFormularioService.criarConfiguracao(dto);
    }

     public Optional<ConfiguracaoFormularioDTO> buscarConfiguracaoPorAcaoId(Long acaoId) {
        return configuracaoFormularioService.buscarConfiguracaoPorAcaoId(acaoId);
    }
}