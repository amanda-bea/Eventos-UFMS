package com.ufms.eventos.services;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarAcaoDTO;
import com.ufms.eventos.dto.AcaoMinDTO;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Usuario;

import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import com.ufms.eventos.repository.EventoRepositoryJDBC;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de Serviço que contém a lógica de negócio para Ações de eventos.
 */
public class AcaoService {
    private AcaoRepositoryJDBC acaoRepository;
    private EventoRepositoryJDBC eventoRepository;
    private PresencaConfirmadaService presencaService = new PresencaConfirmadaService();

    public AcaoService() {
        this.acaoRepository = new AcaoRepositoryJDBC();
        this.eventoRepository = new EventoRepositoryJDBC();
    }

    /**
     * a ser implementado
     */
    public boolean adicionarAcaoEmEventoExistente(Long eventoId, AcaoDTO acaoDTO, Usuario usuarioLogado) {
        try {
            Evento evento = eventoRepository.buscarPorId(eventoId);
            if (evento == null) {
                return false;
            }
            
            if (!evento.getOrganizador().getNome().equals(usuarioLogado.getNome())) {
                return false;
            }
            
            List<Acao> acoesDoEvento = acaoRepository.findByEventoId(eventoId);
            boolean acaoJaExiste = acoesDoEvento.stream()
                    .anyMatch(a -> a.getNome().equalsIgnoreCase(acaoDTO.getNome()));
                    
            if (acaoJaExiste) {
                return false;
            }

            Acao novaAcao = new Acao(acaoDTO);
            novaAcao.setEvento(evento);
            novaAcao.setStatus("Aguardando aprovação");

            return acaoRepository.addAcao(novaAcao);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editarAcao(EditarAcaoDTO dto, Usuario usuarioLogado) {
        Acao acao = acaoRepository.getAcao(dto.getNomeAcao());
        
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

    public boolean deletarAcao(String nomeAcao, Usuario usuarioLogado) {
        Acao acao = acaoRepository.getAcao(nomeAcao);
        
        if (acao != null && acao.getEvento().getOrganizador().equals(usuarioLogado)) {
            return acaoRepository.deleteAcao(nomeAcao);
        }
        return false;
    }
    
    public List<AcaoMinDTO> listarAcoesPorEventoMin(String nomeEvento) {
        try {
            // Busca todos os eventos e filtra pelo nome
            List<Evento> eventos = eventoRepository.listarTodos();
            Evento evento = eventos.stream()
                .filter(e -> e.getNome().equals(nomeEvento))
                .findFirst()
                .orElse(null);
                
            if (evento == null) {
                return new ArrayList<>();
            }
            
            return listarAcoesPorEventoMin(evento.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

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
        List<Acao> acoesDoEvento = acaoRepository.findByEventoId(eventoId);
        // 2. Converte a lista de entidades para uma lista de DTOs e a retorna
        return acoesDoEvento.stream()
                .map(AcaoMinDTO::new)
                .collect(Collectors.toList());
    }
    

    public void verificarStatusVagas(Long idAcao) {
        Acao acao = acaoRepository.findById(idAcao);
        if (acao == null || acao.getCapacidade() <= 0) {
            return;
        }

        int confirmados = presencaService.contarPresencasConfirmadas(idAcao);
        int capacidade = acao.getCapacidade();

        if (confirmados >= capacidade) {
            acao.setStatus("Lotado");
        }

        acaoRepository.updateAcao(acao);
    }

    public List<AcaoDTO> listarAcoesPorEventoComAvisos(Long eventoId) {
        List<Acao> acoesDoEvento = acaoRepository.findByEventoId(eventoId);

        return acoesDoEvento.stream().map(acao -> {
            // Converte a entidade para o DTO COMPLETO
            AcaoDTO dto = new AcaoDTO(acao);
            
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

    public List<AcaoDTO> listarAcoesCompletasPorEvento(Long eventoId) {
        List<Acao> acoesDoEvento = acaoRepository.findByEventoId(eventoId);

        // Converte a lista de entidades para uma lista de DTOs COMPLETOS
        return acoesDoEvento.stream()
                .map(AcaoDTO::new)
                .collect(Collectors.toList());
    }
}