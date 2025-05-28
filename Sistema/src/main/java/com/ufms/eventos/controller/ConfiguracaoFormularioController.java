package com.ufms.eventos.controller;

import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.dto.PresencaConfirmadaDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.ConfiguracaoFormularioService;
import com.ufms.eventos.services.PresencaConfirmadaService;
import com.ufms.eventos.services.UsuarioService;
import com.ufms.eventos.services.AcaoService;
import java.util.Optional; // Para lidar com retornos opcionais do service

public class ConfiguracaoFormularioController {

    private final ConfiguracaoFormularioService configuracaoFormularioService;
    private final PresencaConfirmadaService presencaConfirmadaService;
    private final UsuarioService usuarioService;
    private final AcaoService acaoService;

    public ConfiguracaoFormularioController() {
        // Em uma aplicação real com Spring, por exemplo, as dependências seriam injetadas.
        this.configuracaoFormularioService = new ConfiguracaoFormularioService(new com.ufms.eventos.repository.ConfiguracaoFormularioRepository());
        this.presencaConfirmadaService = new PresencaConfirmadaService();
        this.usuarioService = new UsuarioService();
        this.acaoService = new AcaoService();
    }

    // Construtor para injeção de dependências (melhor prática)
    public ConfiguracaoFormularioController(
            ConfiguracaoFormularioService configuracaoFormularioService,
            PresencaConfirmadaService presencaConfirmadaService,
            UsuarioService usuarioService,
            AcaoService acaoService) {
        this.configuracaoFormularioService = configuracaoFormularioService;
        this.presencaConfirmadaService = presencaConfirmadaService;
        this.usuarioService = usuarioService;
        this.acaoService = acaoService;
    }

    /**
     * Cria uma nova configuração de formulário.
     * @param dto DTO contendo os dados da configuração.
     * @return DTO da configuração criada ou null em caso de erro.
     */
    public ConfiguracaoFormularioDTO criarConfiguracao(ConfiguracaoFormularioDTO dto) {
        // Validações básicas no DTO podem ocorrer aqui ou no início do service.
        if (dto == null || dto.getNomeAcao() == null || dto.getNomeAcao().trim().isEmpty()) {
            System.err.println("ConfiguracaoFormularioController: Nome da ação é obrigatório no DTO.");
            return null; // Ou lançar exceção apropriada (ex: IllegalArgumentException)
        }
        return configuracaoFormularioService.criarConfiguracaoFormulario(dto);
    }

    /**
     * Busca uma configuração de formulário pelo nome da ação.
     * @param nomeAcao Nome da ação.
     * @return DTO da configuração encontrada, ou null se não existir.
     */
    public ConfiguracaoFormularioDTO buscarConfiguracao(String nomeAcao) {
        Optional<ConfiguracaoFormularioDTO> dtoOptional = configuracaoFormularioService.buscarConfiguracaoFormularioPorNomeAcao(nomeAcao);
        return dtoOptional.orElse(null); // Retorna o DTO ou null
    }

    /**
     * Atualiza uma configuração de formulário existente.
     * @param nomeAcao O nome da ação (identificador do recurso).
     * @param dto DTO com os dados atualizados.
     * @return DTO da configuração atualizada, ou null se não encontrada ou erro.
     */
    public ConfiguracaoFormularioDTO atualizarConfiguracao(String nomeAcao, ConfiguracaoFormularioDTO dto) {
         if (dto == null) {
            System.err.println("ConfiguracaoFormularioController: DTO de atualização não pode ser nulo.");
            return null;
        }
        // Garante que o nome da ação no DTO (se presente e importante) esteja alinhado,
        // ou use o nomeAcao do path/parâmetro como a fonte da verdade para o ID.
        // No service, já estamos usando o 'nomeAcao' do parâmetro para identificar o registro a ser atualizado.
        return configuracaoFormularioService.atualizarConfiguracaoFormulario(nomeAcao, dto);
    }

    /**
     * Deleta a configuração de formulário de uma ação.
     * @param nomeAcao Nome da ação.
     * @return true se deletado com sucesso, false caso contrário.
     */
    public boolean deletarConfiguracao(String nomeAcao) {
        return configuracaoFormularioService.deletarConfiguracaoFormulario(nomeAcao);
    }

}