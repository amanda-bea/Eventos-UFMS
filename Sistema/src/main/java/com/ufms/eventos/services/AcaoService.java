package com.ufms.eventos.services;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarAcaoDTO;
import com.ufms.eventos.dto.AcaoMinDTO;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Usuario;

import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de Serviço que contém a lógica de negócio para Ações de eventos.
 */
public class AcaoService {
    private AcaoRepository acaoRepository;
    private EventoRepository eventoRepository;

    public AcaoService() {
        this.acaoRepository = new AcaoRepository();
        this.eventoRepository = new EventoRepository();
    }

    /**
     * CORRIGIDO: Adiciona uma nova ação a um evento que já existe.
     * Recebe o usuário logado para verificar se ele é o dono do evento.
     */
    public boolean adicionarAcaoEmEventoExistente(String nomeEvento, AcaoDTO acaoDTO, Usuario usuarioLogado) {
        Evento evento = eventoRepository.getEvento(nomeEvento);
        if (evento == null) {
            return false; // Evento não existe
        }
        
        // A verificação de permissão agora usa .equals(), que compara os nomes
        if (!evento.getOrganizador().equals(usuarioLogado)) {
            return false; // Usuário não é o dono do evento
        }
        
        // Sua verificação de ação duplicada (pode ser mantida ou alterada conforme sua regra)
        boolean acaoJaExiste = acaoRepository.getAcoes().stream()
                .anyMatch(a -> a.getEvento().equals(evento) && a.getNome().equalsIgnoreCase(acaoDTO.getNome()));
        if (acaoJaExiste) {
            return false;
        }

        Acao novaAcao = new Acao(acaoDTO);
        novaAcao.setEvento(evento);
        novaAcao.setStatus("Aguardando aprovação");

        return acaoRepository.addAcao(novaAcao);
    }

    /**
     * CORRIGIDO: Edita uma ação existente.
     * Recebe o usuário logado para garantir que ele tem permissão.
     */
    public boolean editarAcao(EditarAcaoDTO dto, Usuario usuarioLogado) {
        Acao acao = acaoRepository.getAcao(dto.getNomeAcao());
        
        // A verificação de permissão usa .equals()
        if (acao != null && acao.getEvento().getOrganizador().equals(usuarioLogado)) {
            acao.setData(LocalDate.parse(dto.getNovaData()));
            acao.setLocal(dto.getNovoLocal());
            acao.setHorarioInicio(LocalTime.parse(dto.getNovoHorarioInicio()));
            acao.setHorarioFim(LocalTime.parse(dto.getNovoHorarioFim()));
            acao.setLink(dto.getNovoLink());
            acao.setCapacidade(Integer.parseInt(dto.getNovaCapacidade()));
            acaoRepository.updateAcao(acao);
            return true;
        }
        return false;
    }

    /**
     * CORRIGIDO: Deleta uma ação. Adicionada verificação de permissão.
     */
    public boolean deletarAcao(String nomeAcao, Usuario usuarioLogado) {
        Acao acao = acaoRepository.getAcao(nomeAcao);
        
        if (acao != null && acao.getEvento().getOrganizador().equals(usuarioLogado)) {
            return acaoRepository.deleteAcao(nomeAcao);
        }
        return false;
    }
    
    /**
     * MELHORADO: Busca uma única ação pelo nome e retorna seu DTO.
     * @return O AcaoDTO se encontrado, ou null caso contrário.
     */
    public AcaoDTO getAcaoDtoPorNome(String nome) {
        Acao acao = acaoRepository.getAcao(nome);
        if (acao != null) {
            return new AcaoDTO(acao);
        }
        return null; // Retornar null é mais seguro para UI do que lançar exceção.
    }

    /**
     * Lista todas as ações de um evento específico no formato Mínimo (DTO).
     */
    public List<AcaoMinDTO> listarAcoesPorEventoMin(String nomeEvento) {
        Evento evento = eventoRepository.getEvento(nomeEvento);
        if (evento == null) {
            return new ArrayList<>();
        }
        return acaoRepository.getAcoes().stream()
                .filter(acao -> acao.getEvento().equals(evento))
                .map(AcaoMinDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza o status de ações cuja data já passou.
     */
    public void atualizarAcoesExpiradas() {
        LocalDate hoje = LocalDate.now();
        acaoRepository.getAcoes().stream()
                .filter(acao -> acao.getData().isBefore(hoje) && !acao.getStatus().equals("Cancelado"))
                .forEach(acao -> {
                    acao.setStatus("Inativo");
                    acaoRepository.updateAcao(acao);
                });
    }

     /*

    public void verificarStatusVagas(Long idAcao) {

        Acao acao = acaoRepository.findById(idAcao);
        int confirmados = contarPresencasConfirmadas(idAcao);
        int capacidade = acao.getCapacidade();
        if (confirmados >= capacidade) {
            acao.setStatus("Lotado");
        } else if (capacidade - confirmados <= 10) {
            String label = Últimas vagas
    }
     */
}