package com.ufms.eventos.controller;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarEventoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.EventoService;

import java.util.List;

/**
 * Controller que lida com as operações de usuários e organizadores.
 */
public class EventoController {

    private EventoService eventoService;

    public EventoController() {
        this.eventoService = new EventoService();
    }

    /**
     * Permite que qualquer usuário logado solicite a criação de um evento.
     */
    public boolean solicitarEventoComAcoes(EventoDTO eventoDTO, List<AcaoDTO> listaAcoesDTO, Usuario criadorDoEvento) {
        return eventoService.solicitarEventoComAcoes(eventoDTO, listaAcoesDTO, criadorDoEvento);
    }

    /**
     * Busca todos os eventos criados por um usuário específico para a tela "Meus Eventos".
     */
    public List<EventoMinDTO> buscarEventosPorUsuario(Usuario usuario) {
        return eventoService.buscarEventosPorUsuario(usuario);
    }

    /**
     * Busca os detalhes completos de um evento pelo seu ID.
     */
    public EventoDTO buscarDtoPorId(Long id) {
        return eventoService.buscarDtoPorId(id);
    }

    /**
     * Lista todos os eventos públicos com status "Ativo" para a home do usuário.
     */
    public List<EventoMinDTO> listarEventosAtivosMin() {
        return eventoService.listarEventosAtivosMin();
    }

    /**
     * Permite que um usuário edite um evento que ele criou.
     */
    public boolean editarEvento(EditarEventoDTO dto, Usuario usuarioLogado) {
        return eventoService.editarEvento(dto, usuarioLogado);
    }

    /**
     * Permite que um usuário exclua uma solicitação de evento que ele criou.
     */
    public boolean excluirSolicitacaoEvento(String nomeEvento, Usuario usuarioLogado) {
        return eventoService.excluirSolicitacaoEvento(nomeEvento, usuarioLogado);
    }

    public List<EventoMinDTO> buscarEventosComFiltro(String termoBusca, Categoria categoria, Departamento departamento, String modalidade) {
        return eventoService.buscarEventosComFiltro(termoBusca, categoria, departamento, modalidade);
    }
}