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
    private PresencaConfirmadaService presencaService = new PresencaConfirmadaService();

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

    public List<AcaoMinDTO> listarAcoesPorEventoMin(Long eventoId) {
        // 1. Busca as entidades completas do repositório
        List<Acao> acoesDoEvento = acaoRepository.findByEventoId(eventoId);

        // 2. Converte a lista de entidades para uma lista de DTOs e a retorna
        return acoesDoEvento.stream()
                .map(AcaoMinDTO::new) // Para cada Acao, cria um novo AcaoMinDTO
                .collect(Collectors.toList());
    }
    

    public void verificarStatusVagas(Long idAcao) {
        Acao acao = acaoRepository.findById(idAcao); // Supondo que você tenha findById no repo
        if (acao == null || acao.getCapacidade() <= 0) {
            // Não faz nada se a ação não existe ou tem capacidade ilimitada (<=0)
            return;
        }

        int confirmados = presencaService.contarPresencasConfirmadas(idAcao);
        int capacidade = acao.getCapacidade();

        if (confirmados >= capacidade) {
            acao.setStatus("Lotado");
        }
        // Nota: O status "Últimas vagas" não é um status real da Ação, 
        // é uma informação para a UI. Vamos lidar com isso no DTO.

        acaoRepository.updateAcao(acao); // Salva a mudança de status se houver
    }

    public List<AcaoMinDTO> listarAcoesPorEventoComAvisos(Long eventoId) {
        List<Acao> acoesDoEvento = acaoRepository.findByEventoId(eventoId);

        return acoesDoEvento.stream().map(acao -> {
            // Converte a entidade para o DTO
            AcaoMinDTO dto = new AcaoMinDTO(acao);
            
            // Lógica para adicionar o aviso de vagas
            if (acao.getCapacidade() > 0 && !"Lotado".equalsIgnoreCase(acao.getStatus())) {
                int confirmados = presencaService.contarPresencasConfirmadas(acao.getId());
                int vagasRestantes = acao.getCapacidade() - confirmados;

                if (vagasRestantes > 0 && vagasRestantes <= 10) {
                    dto.setAvisoVagas("ÚLTIMAS VAGAS (" + vagasRestantes + ")");
                }
            } else if ("Lotado".equalsIgnoreCase(acao.getStatus())) {
                dto.setAvisoVagas("ESGOTADO");
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
}