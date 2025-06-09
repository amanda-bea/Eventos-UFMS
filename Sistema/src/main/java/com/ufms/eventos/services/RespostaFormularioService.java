package com.ufms.eventos.services;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.RespostaFormulario;
import com.ufms.eventos.model.Usuario;

import com.ufms.eventos.repository.RespostaFormularioRepository;
import com.ufms.eventos.repository.RespostaFormularioRepositoryJDBC;
import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import com.ufms.eventos.repository.PresencaConfirmadaRepository;
import com.ufms.eventos.repository.PresencaConfirmadaRepositoryJDBC;
import com.ufms.eventos.util.SessaoUsuario;

import java.util.List;

public class RespostaFormularioService {
    private final RespostaFormularioRepository respostaRepository;
    private final AcaoRepositoryJDBC acaoRepository;
    private final PresencaConfirmadaRepository presencaRepository;
    private final AcaoService acaoService;

    public RespostaFormularioService() {
        this.respostaRepository = new RespostaFormularioRepositoryJDBC();
        this.acaoRepository = new AcaoRepositoryJDBC();
        this.presencaRepository = new PresencaConfirmadaRepositoryJDBC();
        this.acaoService = new AcaoService();
    }
    
    /**
     * Construtor para injeção de dependência (facilita testes).
     */
    public RespostaFormularioService(RespostaFormularioRepository respostaRepository, 
                                    AcaoRepositoryJDBC acaoRepository,
                                    PresencaConfirmadaRepository presencaRepository,
                                    AcaoService acaoService) {
        this.respostaRepository = respostaRepository;
        this.acaoRepository = acaoRepository;
        this.presencaRepository = presencaRepository;
        this.acaoService = acaoService;
    }

    /**
     * Salva a resposta de um formulário E cria o registro de presença confirmada,
     * atualizando o status de vagas da ação.
     */
    public boolean salvarResposta(RespostaFormularioDTO dto) {
        // Validação básica
        if (dto == null || dto.getAcaoNome() == null) {
            System.err.println("Dados do formulário inválidos");
            return false;
        }

        // Pega o usuário logado na sessão
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogado == null) {
            System.err.println("Nenhum usuário logado para confirmar presença");
            return false;
        }

        // Encontra a ação à qual a resposta pertence
        Acao acaoAssociada = acaoRepository.getAcao(dto.getAcaoNome());
        if (acaoAssociada == null) {
            System.err.println("Ação '" + dto.getAcaoNome() + "' não encontrada");
            return false;
        }

        // 1. Salva a resposta do formulário
        RespostaFormulario resposta = new RespostaFormulario();
        resposta.setAcao(acaoAssociada);
        resposta.setNome(dto.getNome());
        resposta.setEmail(dto.getEmail());
        resposta.setCpf(dto.getCpf());
        resposta.setCurso(dto.getCurso());
        
        // Adiciona as respostas extras
        if (dto.getRespostasExtras() != null) {
            resposta.setRespostasExtras(dto.getRespostasExtras());
        }
        
        respostaRepository.salvar(resposta);
        System.out.println("Resposta do formulário salva para a ação: " + acaoAssociada.getNome());

        // 2. CRIA E SALVA O REGISTRO DE PRESENÇA 
        PresencaConfirmada novaPresenca = new PresencaConfirmada(usuarioLogado, acaoAssociada);
        boolean sucesso = presencaRepository.addPresencaConfirmada(novaPresenca);
        
        if (sucesso) {
            System.out.println("Presença confirmada para " + usuarioLogado.getNome() + " na ação " + acaoAssociada.getNome());

            // 3. CHAMA O SERVIÇO DA AÇÃO PARA ATUALIZAR O STATUS DE VAGAS
            acaoService.verificarStatusVagas(acaoAssociada.getId());
            return true;
        } else {
            System.out.println("O usuário " + usuarioLogado.getNome() + " já tinha presença confirmada nesta ação");
            return false;
        }
    }
    
    /**
     * Lista todas as respostas para uma ação específica.
     */
    public List<RespostaFormulario> listarRespostasPorAcao(String nomeAcao) {
        if (nomeAcao == null || nomeAcao.trim().isEmpty()) {
            return List.of(); // Lista vazia
        }
        return respostaRepository.listarPorAcao(nomeAcao);
    }
    
    /**
     * Lista todas as respostas para uma ação específica usando o ID.
     */
    public List<RespostaFormulario> listarRespostasPorAcaoId(Long acaoId) {
        if (acaoId == null) {
            return List.of(); // Lista vazia
        }
        return respostaRepository.listarPorAcaoId(acaoId);
    }
}