package com.ufms.eventos.services;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.PresencaConfirmada;
import com.ufms.eventos.model.Usuario;

import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.IPresencaConfirmadaRepository;
import com.ufms.eventos.repository.PresencaConfirmadaRepositoryJDBC;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.dto.PresencaConfirmadaDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PresencaConfirmadaService {
    private IPresencaConfirmadaRepository pr;
    private AcaoRepository ar;

    public PresencaConfirmadaService() {
        this.pr = new PresencaConfirmadaRepositoryJDBC();
        this.ar = new AcaoRepository();
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

    public int contarPresencasConfirmadas(Long idAcao) {
        return (int) pr.countByAcaoId(idAcao);
    }

    public List<EventoMinDTO> listarEventosComPresencaConfirmada(Usuario usuario) {
        if (usuario == null) {
            return new ArrayList<>();
        }

        return pr.getPresencasConfirmadas().stream()
                // 1. Filtra as presenças que são do usuário logado
                .filter(presenca -> usuario.equals(presenca.getUsuario()))
                // 2. Para cada presença, pega o Evento pai da Ação
                .map(presenca -> presenca.getAcao().getEvento())
                // 3. Garante que cada evento apareça apenas uma vez na lista
                .distinct()
                // 4. Converte os objetos Evento para EventoMinDTO
                .map(EventoMinDTO::new)
                // 5. Coleta tudo em uma lista
                .collect(Collectors.toList());
    }
}
