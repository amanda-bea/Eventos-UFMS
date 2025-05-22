package com.ufms.eventos.services;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.EditarAcaoDTO;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.model.Evento;

import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AcaoService {
    private AcaoRepository acaoRepository;
    private EventoRepository eventoRepository;

    public AcaoService() {
        this.acaoRepository = new AcaoRepository();
        this.eventoRepository = new EventoRepository();
    }

    public boolean solicitarAcao(AcaoDTO acaoDTO, Organizador organizador, EventoDTO eventoDTO) {

        Evento evento = new Evento(eventoDTO);
        evento.setStatus("Aguardando aprovação");
        evento.setOrganizador(organizador);
        evento.setMensagemRejeicao(null);

            // Se o evento ainda não existe no repositório, adiciona
        if (eventoRepository.getEvento(evento.getNome()) == null) {
            eventoRepository.addEvento(evento);
        } 
        else {
        }

        Acao acao = new Acao(acaoDTO);
        acao.setEvento(evento);
        acao.setStatus("Aguardando aprovação");
        acao.setMensagemRejeicao(null);
        
        return acaoRepository.addAcao(acao);
    }

    public HashSet<AcaoDTO> listarAcoes() {
        HashSet<Acao> acoes = acaoRepository.getAcoes();
        HashSet<AcaoDTO> dto = acoes.stream()
                               .map(x -> new AcaoDTO(x))
                               .collect(Collectors.toCollection(HashSet::new));
        return dto;
    }

    public boolean deletarAcao(String nome) {
        // Deleta a ação do repositório
        return acaoRepository.deleteAcao(nome);
    }

    public AcaoDTO getAcao(String nome) {
        Acao acao = acaoRepository.getAcao(nome);
        if (acao != null) {
            return new AcaoDTO(acao);
        }
        throw new IllegalArgumentException("Ação não existe.");
    }

    public boolean editarAcao(EditarAcaoDTO dto, Organizador organizador) {
        Acao acao = acaoRepository.getAcao(dto.getNomeAcao());
        if (acao != null && acao.getEvento().getOrganizador().equals(organizador)) {
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

    public boolean adicionarAcaoEmEventoExistente(String nomeEvento, AcaoDTO acaoDTO, Organizador organizador) {
        Evento evento = eventoRepository.getEvento(nomeEvento);
        if (evento == null) {
            return false; // Evento não existe
        }
        // Garante que o organizador é o dono do evento
        if (!evento.getOrganizador().equals(organizador)) {
            return false;
        }
        // Verifica se já existe uma ação com o mesmo nome neste evento
        boolean acaoJaExiste = acaoRepository.getAcoes().stream()
                .anyMatch(a -> a.getEvento().equals(evento) && a.getNome().equalsIgnoreCase(acaoDTO.getNome()));
        if (acaoJaExiste) {
            return false; // Já existe uma ação com esse nome neste evento MUDAR REGRA AQUI
        }

        Acao novaAcao = new Acao(acaoDTO);
        novaAcao.setEvento(evento);
        novaAcao.setStatus("Aguardando aprovação");
        novaAcao.setMensagemRejeicao(null);

        return acaoRepository.addAcao(novaAcao);
    }

}
