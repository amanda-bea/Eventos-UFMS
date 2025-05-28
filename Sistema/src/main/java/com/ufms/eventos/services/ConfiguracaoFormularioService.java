package com.ufms.eventos.services;

import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.model.ConfiguracaoFormulario;
import com.ufms.eventos.repository.ConfiguracaoFormularioRepository;
import java.util.ArrayList;

import java.util.Optional;

public class ConfiguracaoFormularioService {

    private final ConfiguracaoFormularioRepository repository;

    public ConfiguracaoFormularioService() {
        this.repository = new ConfiguracaoFormularioRepository();
    }

    public ConfiguracaoFormularioService(ConfiguracaoFormularioRepository repository) {
        this.repository = repository;
    }

    public ConfiguracaoFormularioDTO criarConfiguracaoFormulario(ConfiguracaoFormularioDTO dto) {
        if (dto == null || dto.getNomeAcao() == null || dto.getNomeAcao().trim().isEmpty()) {
            System.err.println("ConfiguracaoFormularioService: Dados inválidos para criar configuração.");
            return null;
        }

        // Converte DTO para Model
        ConfiguracaoFormulario model = new ConfiguracaoFormulario();
        model.setNomeAcao(dto.getNomeAcao());
        model.setUsarNome(dto.isUsarNome());
        model.setUsarEmail(dto.isUsarEmail());
        model.setUsarCpf(dto.isUsarCpf());
        model.setUsarCurso(dto.isUsarCurso());
        model.setNomesCamposPersonalizados(new ArrayList<>(dto.getNomesCamposPersonalizados())); // Copia a lista

        ConfiguracaoFormulario salvo = repository.salvar(model);
        if (salvo != null) {
            // Converte Model de volta para DTO para o retorno
            return new ConfiguracaoFormularioDTO(
                salvo.getNomeAcao(),
                salvo.isUsarNome(),
                salvo.isUsarEmail(),
                salvo.isUsarCpf(),
                salvo.isUsarCurso(),
                new ArrayList<>(salvo.getNomesCamposPersonalizados())
            );
        }
        return null;
    }

    public Optional<ConfiguracaoFormularioDTO> buscarConfiguracaoFormularioPorNomeAcao(String nomeAcao) {
        Optional<ConfiguracaoFormulario> modelOptional = repository.buscarPorNomeAcao(nomeAcao);
        if (modelOptional.isPresent()) {
            ConfiguracaoFormulario model = modelOptional.get();
            // Converte Model para DTO
            ConfiguracaoFormularioDTO dto = new ConfiguracaoFormularioDTO(
                model.getNomeAcao(),
                model.isUsarNome(),
                model.isUsarEmail(),
                model.isUsarCpf(),
                model.isUsarCurso(),
                new ArrayList<>(model.getNomesCamposPersonalizados())
            );
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public boolean deletarConfiguracaoFormulario(String nomeAcao) {
        return repository.deletarPorNomeAcao(nomeAcao);
    }
}
    