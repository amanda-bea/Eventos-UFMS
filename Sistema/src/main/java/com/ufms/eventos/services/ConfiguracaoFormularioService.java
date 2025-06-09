package com.ufms.eventos.services;

import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.model.ConfiguracaoFormulario;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.repository.ConfiguracaoFormularioRepositoryJDBC;
import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Serviço para gerenciar configurações de formulários.
 */
public class ConfiguracaoFormularioService {

    private final ConfiguracaoFormularioRepositoryJDBC repository;
    private final AcaoRepositoryJDBC acaoRepository;

    /**
     * Construtor padrão que inicializa os repositórios.
     */
    public ConfiguracaoFormularioService() {
        this.repository = new ConfiguracaoFormularioRepositoryJDBC();
        this.acaoRepository = new AcaoRepositoryJDBC();
    }

    /**
     * Cria uma nova configuração de formulário.
     * @param dto O DTO contendo os dados da configuração.
     * @return O DTO da configuração criada, ou null se ocorreu algum erro.
     */
    public ConfiguracaoFormularioDTO criarConfiguracaoFormulario(ConfiguracaoFormularioDTO dto) {
        if (dto == null || dto.getNomeAcao() == null || dto.getNomeAcao().trim().isEmpty()) {
            System.err.println("ConfiguracaoFormularioService: Dados inválidos para criar configuração.");
            return null;
        }

        // Busca a ação pelo nome
        Acao acao = acaoRepository.getAcao(dto.getNomeAcao());
        if (acao == null) {
            System.err.println("Ação não encontrada: " + dto.getNomeAcao());
            return null;
        }

        // Converte DTO para Model
        ConfiguracaoFormulario model = new ConfiguracaoFormulario();
        model.setAcao(acao);
        model.setUsarNome(dto.isUsarNome());
        model.setUsarEmail(dto.isUsarEmail());
        model.setUsarCpf(dto.isUsarCpf());
        model.setUsarCurso(dto.isUsarCurso());
        model.setCamposPersonalizados(new ArrayList<>(dto.getCamposPersonalizados())); // Usando o nome correto

        ConfiguracaoFormulario salvo = repository.salvar(model);
        if (salvo != null) {
            // Converte Model de volta para DTO para o retorno
            return new ConfiguracaoFormularioDTO(
                salvo.getAcao().getNome(),
                salvo.isUsarNome(),
                salvo.isUsarEmail(),
                salvo.isUsarCpf(),
                salvo.isUsarCurso(),
                new ArrayList<>(salvo.getCamposPersonalizados())
            );
        }
        return null;
    }

    /**
     * Busca uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return Um Optional contendo o DTO da configuração, se encontrada.
     */
    public Optional<ConfiguracaoFormularioDTO> buscarConfiguracaoFormularioPorNomeAcao(String nomeAcao) {
        Optional<ConfiguracaoFormulario> modelOptional = repository.buscarPorNomeAcao(nomeAcao);
        if (modelOptional.isPresent()) {
            ConfiguracaoFormulario model = modelOptional.get();
            // Converte Model para DTO
            ConfiguracaoFormularioDTO dto = new ConfiguracaoFormularioDTO(
                model.getAcao().getNome(),
                model.isUsarNome(),
                model.isUsarEmail(),
                model.isUsarCpf(),
                model.isUsarCurso(),
                new ArrayList<>(model.getCamposPersonalizados())
            );
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    /**
     * Busca uma configuração de formulário pelo ID da ação.
     * @param acaoId O ID da ação.
     * @return Um Optional contendo o DTO da configuração, se encontrada.
     */
    public Optional<ConfiguracaoFormularioDTO> buscarConfiguracaoFormularioPorAcaoId(Long acaoId) {
        Optional<ConfiguracaoFormulario> modelOptional = repository.buscarPorAcaoId(acaoId);
        if (modelOptional.isPresent()) {
            ConfiguracaoFormulario model = modelOptional.get();
            // Converte Model para DTO
            ConfiguracaoFormularioDTO dto = new ConfiguracaoFormularioDTO(
                model.getAcao().getNome(),
                model.isUsarNome(),
                model.isUsarEmail(),
                model.isUsarCpf(),
                model.isUsarCurso(),
                new ArrayList<>(model.getCamposPersonalizados())
            );
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    /**
     * Deleta uma configuração de formulário pelo nome da ação.
     * @param nomeAcao O nome da ação.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean deletarConfiguracaoFormulario(String nomeAcao) {
        return repository.deletarPorNomeAcao(nomeAcao);
    }

    /**
     * Deleta uma configuração de formulário pelo ID da ação.
     * @param acaoId O ID da ação.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean deletarConfiguracaoFormularioPorAcaoId(Long acaoId) {
        return repository.deletarPorAcaoId(acaoId);
    }

    /**
     * Verifica se existe uma configuração para a ação especificada.
     * @param nomeAcao O nome da ação.
     * @return true se existir, false caso contrário.
     */
    public boolean existeConfiguracaoFormulario(String nomeAcao) {
        return repository.existePorNomeAcao(nomeAcao);
    }
}