package com.ufms.eventos.services;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.Usuario;

import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.PresencaConfirmadaRepository;

import com.ufms.eventos.dto.PresencaConfirmadaDTO; // If you use this DTO elsewhere

import java.util.List;
import java.util.stream.Collectors;

public class PresencaConfirmadaService {
    private PresencaConfirmadaRepository pr;
    private AcaoRepository ar;

    public PresencaConfirmadaService() {
        this.pr = new PresencaConfirmadaRepository();
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

        // 4. Criar o objeto de presença
        PresencaConfirmada presencaParaConfirmar = new PresencaConfirmada(usuario, acao);

        // 5. Tentar adicionar a presença ao repositório.
        boolean foiAdicionadaNovaPresenca = pr.addPresencaConfirmada(presencaParaConfirmar);

        if (foiAdicionadaNovaPresenca) {
            System.out.println("Presença confirmada com sucesso para o usuário: " + usuario.getNome() +
                            " na ação: " + acao.getNome() + ".");
            return true;
        } else {
            System.out.println("Usuário '" + usuario.getNome() + "' já possui presença confirmada anteriormente para a ação '" +
                            acao.getNome() + "'. Nenhuma nova ação necessária.");
            return true;
        }
    }

    public List<PresencaConfirmadaDTO> listarPresencasPorUsuario(String nomeUsuario) {
        List<PresencaConfirmada> presencas = pr.getPresencasConfirmadas()
            .stream()
            .filter(p -> p.getUsuario().getNome().equalsIgnoreCase(nomeUsuario))
            .filter(p -> "Ativo".equalsIgnoreCase(p.getAcao().getStatus())) // Considere se "Lotado" também deve ser listado se o usuário já estiver inscrito.
            .collect(Collectors.toList());

        return presencas.stream()
            .map(p -> new PresencaConfirmadaDTO(
                p.getUsuario().getNome(),
                p.getAcao().getNome()))
            .collect(Collectors.toList());
    }
}