package com.ufms.eventos.services;

import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.model.ConfiguracaoFormulario;
import com.ufms.eventos.repository.ConfiguracaoFormularioRepository;
import java.util.ArrayList;

import java.util.Optional;

public class ConfiguracaoFormularioService {

    private final ConfiguracaoFormularioRepository repository;

    public ConfiguracaoFormularioService() {
        this.repository = new ConfiguracaoFormularioRepository(); // Injeção de dependência seria melhor em projetos maiores
    }

    // Construtor para injeção de dependência (melhor prática)
    public ConfiguracaoFormularioService(ConfiguracaoFormularioRepository repository) {
        this.repository = repository;
    }

    /**
     * Cria e salva uma nova configuração de formulário a partir de um DTO.
     * @param dto O DTO com os dados da configuração.
     * @return O DTO da configuração salva, ou null em caso de falha.
     */
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

    /**
     * Busca uma configuração de formulário pelo nome da ação e a retorna como DTO.
     * @param nomeAcao O nome da ação.
     * @return Um Optional contendo o DTO se encontrado, ou Optional.empty() caso contrário.
     */
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

    /**
     * Atualiza uma configuração de formulário existente.
     * @param nomeAcao O nome da ação a ser atualizada.
     * @param dtoAtualizado O DTO com os novos dados da configuração.
     * @return O DTO da configuração atualizada, ou null se a configuração não existir ou em caso de falha.
     */
    public ConfiguracaoFormularioDTO atualizarConfiguracaoFormulario(String nomeAcao, ConfiguracaoFormularioDTO dtoAtualizado) {
        if (!repository.existePorNomeAcao(nomeAcao)) {
            System.err.println("ConfiguracaoFormularioService: Tentativa de atualizar configuração inexistente para a ação: " + nomeAcao);
            return null;
        }
        // Garante que o nome da ação no DTO seja o mesmo da URL/path (se aplicável) ou o identificador correto.
        // Em muitos casos, o nomeAcao do DTO deve corresponder ao parâmetro nomeAcao.
        // Para esta implementação, vamos assumir que o dtoAtualizado.getNomeAcao() é o correto e pode ser diferente
        // se a intenção for renomear a chave, mas isso é complexo.
        // Mais simples: o nomeAcao é fixo, apenas o conteúdo muda.
        if (!nomeAcao.equals(dtoAtualizado.getNomeAcao())) {
             System.err.println("ConfiguracaoFormularioService: Discrepância no nome da ação ao atualizar.");
             // Poderia tratar isso, ou lançar exceção. Por simplicidade, não vamos permitir mudança de nomeAcao via update aqui.
             // Se precisar mudar o nomeAcao, seria um delete + create ou um método específico.
        }


        ConfiguracaoFormulario model = new ConfiguracaoFormulario();
        model.setNomeAcao(nomeAcao); // Usa o nomeAcao como identificador chave
        model.setUsarNome(dtoAtualizado.isUsarNome());
        model.setUsarEmail(dtoAtualizado.isUsarEmail());
        model.setUsarCpf(dtoAtualizado.isUsarCpf());
        model.setUsarCurso(dtoAtualizado.isUsarCurso());
        model.setNomesCamposPersonalizados(new ArrayList<>(dtoAtualizado.getNomesCamposPersonalizados()));

        ConfiguracaoFormulario atualizado = repository.salvar(model); // Salvar substitui o valor existente no Map
         if (atualizado != null) {
            return new ConfiguracaoFormularioDTO(
                atualizado.getNomeAcao(),
                atualizado.isUsarNome(),
                atualizado.isUsarEmail(),
                atualizado.isUsarCpf(),
                atualizado.isUsarCurso(),
                new ArrayList<>(atualizado.getNomesCamposPersonalizados())
            );
        }
        return null;
    }


    /**
     * Deleta a configuração de formulário para uma ação específica.
     * @param nomeAcao O nome da ação.
     * @return true se a configuração foi deletada com sucesso, false caso contrário.
     */
    public boolean deletarConfiguracaoFormulario(String nomeAcao) {
        return repository.deletarPorNomeAcao(nomeAcao);
    }
}