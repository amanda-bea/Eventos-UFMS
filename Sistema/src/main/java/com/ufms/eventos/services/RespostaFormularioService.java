package com.ufms.eventos.services;

import com.ufms.eventos.dto.RespostaFormularioDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.RespostaFormulario;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.PresencaConfirmadaRepository;
import com.ufms.eventos.repository.RespostaFormularioRepository;
import com.ufms.eventos.util.SessaoUsuario;

public class RespostaFormularioService {

    private final RespostaFormularioRepository respostaRepository;
    private final PresencaConfirmadaRepository presencaRepository;
    private final AcaoRepository acaoRepository;
    private final AcaoService acaoService; // Para chamar a verificação de vagas

    public RespostaFormularioService() {
        this.respostaRepository = new RespostaFormularioRepository();
        this.presencaRepository = new PresencaConfirmadaRepository();
        this.acaoRepository = new AcaoRepository();
        this.acaoService = new AcaoService(); // Precisa de acesso ao serviço de Ação
    }

    /**
     * Salva a resposta de um formulário E cria o registro de presença confirmada,
     * atualizando o status de vagas da ação.
     */
    public void salvarResposta(RespostaFormularioDTO dto) {
        // Pega o usuário logado na sessão
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogado == null) {
            throw new IllegalStateException("Nenhum usuário logado para confirmar presença.");
        }

        // Encontra a ação à qual a resposta pertence
        Acao acaoAssociada = acaoRepository.getAcao(dto.getAcaoNome());
        if (acaoAssociada == null) {
            throw new IllegalArgumentException("Ação '" + dto.getAcaoNome() + "' não encontrada.");
        }

        // 1. Salva a resposta do formulário (como antes)
        RespostaFormulario resposta = new RespostaFormulario();
        resposta.setAcao(acaoAssociada);
        resposta.setNome(dto.getNome());
        resposta.setEmail(dto.getEmail());
        // ... setar outros campos ...
        respostaRepository.salvar(resposta);
        System.out.println("Resposta do formulário salva para a ação: " + acaoAssociada.getNome());

        // 2. CRIA E SALVA O REGISTRO DE PRESENÇA 
        PresencaConfirmada novaPresenca = new PresencaConfirmada(usuarioLogado, acaoAssociada);
        boolean sucesso = presencaRepository.addPresencaConfirmada(novaPresenca);
        
        if(sucesso) {
            System.out.println("Presença confirmada para " + usuarioLogado.getNome() + " na ação " + acaoAssociada.getNome());

            // 3. CHAMA O SERVIÇO DA AÇÃO PARA ATUALIZAR O STATUS DE VAGAS
            acaoService.verificarStatusVagas(acaoAssociada.getId());
        } else {
            System.out.println("O usuário " + usuarioLogado.getNome() + " já tinha presença confirmada nesta ação.");
        }
    }
}