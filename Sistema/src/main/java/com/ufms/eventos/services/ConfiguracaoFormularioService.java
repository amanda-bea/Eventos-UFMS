package com.ufms.eventos.services;

import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.model.ConfiguracaoFormulario;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.repository.ConfiguracaoFormularioRepositoryJDBC;
import com.ufms.eventos.repository.AcaoRepositoryJDBC;
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

    public ConfiguracaoFormularioDTO criarConfiguracao(ConfiguracaoFormularioDTO dto) {
        if (dto == null || dto.getAcaoId() == null) return null;

        Acao acao = acaoRepository.findById(dto.getAcaoId());
        if (acao == null) {
            return null; // Ação não existe, não pode criar configuração
        }

        ConfiguracaoFormulario model = new ConfiguracaoFormulario();
        model.setAcao(acao);
        model.setUsarNome(dto.isUsarNome());
        model.setUsarEmail(dto.isUsarEmail());
        model.setUsarCpf(dto.isUsarCpf());
        model.setUsarCurso(dto.isUsarCurso());
        model.setCamposPersonalizados(dto.getCamposPersonalizados());

        ConfiguracaoFormulario salvo = repository.salvar(model);
        return salvo != null ? new ConfiguracaoFormularioDTO(salvo) : null;
    }

    public Optional<ConfiguracaoFormularioDTO> buscarConfiguracaoPorAcaoId(Long acaoId) {
        if (acaoId == null) {
            return Optional.empty();
        }
        // O repositório retorna Optional<Model>
        Optional<ConfiguracaoFormulario> modelOptional = repository.buscarPorAcaoId(acaoId);
        
        // Se o modelo existir, mapeia para um DTO e retorna dentro de um Optional
        return modelOptional.map(ConfiguracaoFormularioDTO::new);
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
     * Busca uma configuração de formulário pelo nome da ação associada
     * 
     * @param nomeAcao Nome da ação associada à configuração de formulário
     * @return Um Optional contendo o DTO da configuração de formulário, ou Optional vazio se não encontrado
     */
    public Optional<ConfiguracaoFormularioDTO> buscarConfiguracaoFormularioPorNomeAcao(String nomeAcao) {
        try {
            // Obter a entidade de configuração do repositório
            Optional<ConfiguracaoFormulario> configuracaoOpt = repository.buscarPorNomeAcao(nomeAcao);
            
            // Se a configuração não foi encontrada, retorne um Optional vazio
            if (!configuracaoOpt.isPresent()) {
                return Optional.empty();
            }
            
            ConfiguracaoFormulario configuracao = configuracaoOpt.get();
            
            // Usar o construtor de conveniência do DTO que já faz o mapeamento correto
            ConfiguracaoFormularioDTO dto = new ConfiguracaoFormularioDTO(configuracao);
            
            // Retornar o DTO dentro de um Optional
            return Optional.of(dto);
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar configuração de formulário: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
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