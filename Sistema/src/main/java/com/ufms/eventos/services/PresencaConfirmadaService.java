package com.ufms.eventos.services;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.Usuario;

import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import com.ufms.eventos.repository.PresencaConfirmadaRepositoryJDBC;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.dto.PresencaConfirmadaDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PresencaConfirmadaService {
    private PresencaConfirmadaRepositoryJDBC pr;
    private AcaoRepositoryJDBC ar;

    public PresencaConfirmadaService() {
        this.pr = new PresencaConfirmadaRepositoryJDBC();
        this.ar = new AcaoRepositoryJDBC();
    }

    public boolean confirmarPresenca(PresencaConfirmadaDTO dto) {
        // 1. Converter DTO para model
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getUsuario());

        Acao acao = ar.getAcao(dto.getAcao());
        if (acao == null) {
            System.err.println("Erro ao confirmar presença: ação não encontrada.");
            return false;
        }

        // 2. Validação básica dos dados de entrada
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            System.err.println("Erro ao confirmar presença: nome do usuário ausente.");
            return false;
        }
        if (acao.getNome() == null || acao.getNome().trim().isEmpty()) {
            System.err.println("Erro ao confirmar presença: nome da ação ausente.");
            return false;
        }

        // 3. Verificar status da ação (deve estar "Ativo")
        if (acao.getStatus() == null || !"Ativo".equalsIgnoreCase(acao.getStatus())) {
            System.err.println("Erro ao confirmar presença: ação não está ativa.");
            return false;
        }
        
        // 3.1 Verificar se já existe uma presença confirmada
        if (pr.verificarPresenca(usuario.getNome(), acao.getId())) {
            System.out.println("Usuário '" + usuario.getNome() + "' já possui presença confirmada anteriormente para a ação '" +
                           acao.getNome() + "'. Nenhuma nova ação necessária.");
            return true;
        }

        // 4. Criar o objeto de presença
        PresencaConfirmada presencaParaConfirmar = new PresencaConfirmada(usuario, acao);

        // 5. Tentar adicionar a presença ao repositório.
        boolean foiAdicionadaNovaPresenca = pr.addPresencaConfirmada(presencaParaConfirmar);

        if (foiAdicionadaNovaPresenca) {
            System.out.println("Presença confirmada com sucesso para o usuário: " + usuario.getNome() +
                           " na ação: " + acao.getNome() + ".");
            return true;
        } else {
            System.err.println("Erro ao confirmar presença para o usuário: " + usuario.getNome());
            return false;
        }
    }

    public int contarPresencasConfirmadas(Long idAcao) {
        return pr.contarPresencasPorAcao(idAcao);
    }

    public List<EventoMinDTO> listarEventosComPresencaConfirmada(Usuario usuario) {
        if (usuario == null || usuario.getNome() == null) {
            return new ArrayList<>();
        }

        // Busca presenças específicas do usuário em vez de filtrar todas
        HashSet<PresencaConfirmada> presencasDoUsuario = pr.getPresencasPorUsuario(usuario.getNome());
        
        return presencasDoUsuario.stream()
                // Para cada presença, pega o Evento pai da Ação
                .map(presenca -> presenca.getAcao().getEvento())
                // Garante que cada evento apareça apenas uma vez na lista
                .distinct()
                // Converte os objetos Evento para EventoMinDTO
                .map(EventoMinDTO::new)
                // Coleta tudo em uma lista
                .collect(Collectors.toList());
    }
    
    /**
     * Lista todas as ações em que o usuário confirmou presença.
     * 
     * @param usuario O usuário para buscar presenças
     * @return Lista de ações com presença confirmada
     */
    public List<Acao> listarAcoesComPresencaConfirmada(Usuario usuario) {
        if (usuario == null || usuario.getNome() == null) {
            return new ArrayList<>();
        }
        
        HashSet<PresencaConfirmada> presencasDoUsuario = pr.getPresencasPorUsuario(usuario.getNome());
        
        return presencasDoUsuario.stream()
                .map(PresencaConfirmada::getAcao)
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica se um usuário específico já confirmou presença em uma ação.
     * @param usuario O usuário da sessão.
     * @param acaoId O ID da ação a ser verificada.
     * @return true se a presença já existe, false caso contrário.
     */
    public boolean isUsuarioInscrito(Usuario usuario, Long acaoId) {
        if (usuario == null || acaoId == null) {
            return false;
        }
        // Supondo que seu PresencaConfirmadaRepository tenha um método 'exists'
        return pr.verificarPresenca(usuario.getNome(), acaoId);
    }

    /**
 * Remove a presença confirmada de um usuário em uma ação.
 * @param usuario O usuário da sessão.
 * @param acaoId O ID da ação da qual o usuário quer se desinscrever.
 * @return true se a inscrição foi cancelada com sucesso.
 */
public boolean cancelarInscricao(Usuario usuario, Long acaoId) {
    if (usuario == null || acaoId == null) {
        return false;
    }
    
    // Supondo que seu PresencaConfirmadaRepository tenha um método 'delete'
    boolean sucesso = pr.delete(usuario.getNome(), acaoId);
    
    if (sucesso) {
        // Em vez de chamar acaoService.verificarStatusVagas(acaoId),
        // atualizamos diretamente o status da ação
        atualizarStatusVagas(acaoId);
    }
    return sucesso;
}

/**
 * Atualiza o status de vagas da ação com base nas inscrições atuais.
 * Se capacidade - inscrições <= 10, status = "Últimas vagas"
 * Se inscrições >= capacidade, status = "Lotado"
 * Caso contrário, status = "Ativo"
 * 
 * @param acaoId ID da ação para atualizar o status
 */
private void atualizarStatusVagas(Long acaoId) {
    Acao acao = ar.findById(acaoId);
    if (acao == null) return;
    
    int inscricoes = contarPresencasConfirmadas(acaoId);
    int capacidade = acao.getCapacidade();
    
    // Se a ação não tem limite de capacidade, mantenha como ativa
    if (capacidade <= 0) {
        if (!"Ativo".equals(acao.getStatus())) {
            acao.setStatus("Ativo");
            ar.updateAcao(acao);
        }
        return;
    }
    
    // Vagas disponíveis
    int vagasDisponiveis = capacidade - inscricoes;
    
    // Definir o status baseado nas vagas disponíveis
    String novoStatus;
    
    if (vagasDisponiveis <= 0) {
        novoStatus = "Lotado";
    } else if (vagasDisponiveis <= 10) {
        novoStatus = "Últimas vagas";
    } else {
        novoStatus = "Ativo";
    }
    
    // Atualizar apenas se o status mudou
    if (!novoStatus.equals(acao.getStatus())) {
        acao.setStatus(novoStatus);
        ar.updateAcao(acao);
        System.out.println("Status da ação ID " + acaoId + " atualizado para: " + novoStatus);
    }
}
}