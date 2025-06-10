package com.ufms.eventos.services;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.model.*;
import com.ufms.eventos.repository.*;
import com.ufms.eventos.util.SessaoUsuario;

public class RespostaFormularioService {

    private final RespostaFormularioRepository respostaRepository;
    private final PresencaConfirmadaRepository presencaRepository;
    private final AcaoRepository acaoRepository;
    private final AcaoService acaoService;

    public RespostaFormularioService() {
        // Inicialize seus repositórios JDBC aqui
        this.respostaRepository = new RespostaFormularioRepositoryJDBC();
        this.presencaRepository = new PresencaConfirmadaRepositoryJDBC();
        this.acaoRepository = new AcaoRepositoryJDBC();
        this.acaoService = new AcaoService();
    }

    public boolean salvarResposta(RespostaFormularioDTO dto) {
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogado == null) {
            throw new IllegalStateException("Nenhum usuário logado para confirmar presença.");
        }
        
        Acao acaoAssociada = acaoRepository.findById(dto.getAcaoId());
        if (acaoAssociada == null) {
            throw new IllegalArgumentException("Ação com ID " + dto.getAcaoId() + " não encontrada.");
        }

        // Cria resposta manualmente em vez de usar o construtor inexistente
        RespostaFormulario resposta = new RespostaFormulario();
        resposta.setAcao(acaoAssociada);
        resposta.setNome(dto.getNome());
        resposta.setEmail(dto.getEmail());
        resposta.setCpf(dto.getCpf());
        resposta.setCurso(dto.getCurso());
        resposta.setRespostasExtras(dto.getRespostasExtras());
        
        respostaRepository.salvar(resposta);

        // 2. Cria e salva o registro de presença
        PresencaConfirmada novaPresenca = new PresencaConfirmada(usuarioLogado, acaoAssociada);
        if (presencaRepository.addPresencaConfirmada(novaPresenca)) {
            // 3. Verifica e atualiza o status de vagas
            acaoService.verificarStatusVagas(acaoAssociada.getId());
            return true;
        }
        
        return false; // Retorna falso se o usuário já estava inscrito
    }
}