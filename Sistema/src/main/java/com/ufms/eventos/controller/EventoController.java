package com.ufms.eventos.controller;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarEventoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.EventoService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller que lida com as operações de usuários e organizadores.
 */
public class EventoController {

    private final EventoService eventoService;

    /**
     * Construtor padrão que inicializa o serviço de eventos.
     */
    public EventoController() {
        this.eventoService = new EventoService();
    }
    
    /**
     * Construtor para injeção de dependência (facilita testes).
     * @param eventoService O serviço de eventos a ser utilizado.
     */
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    /**
     * Permite que qualquer usuário logado solicite a criação de um evento.
     * @param eventoDTO Dados do evento a ser criado.
     * @param listaAcoesDTO Lista de ações associadas ao evento.
     * @param criadorDoEvento Usuário que está criando o evento.
     * @return true se a solicitação foi realizada com sucesso, false caso contrário.
     */
    public boolean solicitarEventoComAcoes(EventoDTO eventoDTO, List<AcaoDTO> listaAcoesDTO, Usuario criadorDoEvento) {
        if (eventoDTO == null || criadorDoEvento == null) {
            return false;
        }
        return eventoService.solicitarEventoComAcoes(eventoDTO, listaAcoesDTO, criadorDoEvento);
    }

    /**
     * Busca todos os eventos criados por um usuário específico para a tela "Meus Eventos".
     * @param usuario O usuário cujos eventos serão buscados.
     * @return Lista de DTOs com informações básicas dos eventos do usuário.
     */
    public List<EventoMinDTO> buscarEventosPorUsuario(Usuario usuario) {
        if (usuario == null) {
            return new ArrayList<>();
        }
        return eventoService.buscarEventosPorUsuario(usuario);
    }

    /**
     * Busca os detalhes completos de um evento pelo seu ID.
     * @param id ID do evento a ser buscado.
     * @return DTO com detalhes completos do evento, ou null se não encontrado.
     */
    public EventoDTO buscarDtoPorId(Long id) {
        if (id == null) {
            return null;
        }
        return eventoService.buscarDtoPorId(id);
    }

    /**
     * Lista todos os eventos públicos com status "Ativo" para a home do usuário.
     * @return Lista de DTOs com informações básicas dos eventos ativos.
     */
    public List<EventoMinDTO> listarEventosAtivosMin() {
        return eventoService.listarEventosAtivosMin();
    }

    /**
     * Permite que um usuário edite um evento que ele criou.
     * @param dto Dados atualizados do evento.
     * @param usuarioLogado Usuário que está tentando editar o evento.
     * @return true se a edição foi realizada com sucesso, false caso contrário.
     */
    public boolean editarEvento(EditarEventoDTO dto, Usuario usuarioLogado) {
        if (dto == null || usuarioLogado == null) {
            return false;
        }
        return eventoService.editarEvento(dto, usuarioLogado);
    }

    /**
     * Permite que um usuário exclua uma solicitação de evento que ele criou.
     * @param nomeEvento Nome do evento a ser excluído.
     * @param usuarioLogado Usuário que está tentando excluir o evento.
     * @return true se a exclusão foi realizada com sucesso, false caso contrário.
     */
    public boolean excluirSolicitacaoEvento(String nomeEvento, Usuario usuarioLogado) {
        if (nomeEvento == null || nomeEvento.trim().isEmpty() || usuarioLogado == null) {
            return false;
        }
        return eventoService.excluirSolicitacaoEvento(nomeEvento, usuarioLogado);
    }

    /**
     * Busca eventos com base em filtros especificados.
     * @param termoBusca Termo para busca por nome ou descrição (opcional).
     * @param categoria Categoria do evento (opcional).
     * @param departamento Departamento do evento (opcional).
     * @param modalidade Modalidade do evento (presencial/online/híbrido) (opcional).
     * @return Lista de DTOs com informações básicas dos eventos que atendem aos filtros.
     */
    public List<EventoMinDTO> buscarEventosComFiltro(String termoBusca, Categoria categoria, Departamento departamento, String modalidade) {
        return eventoService.buscarEventosComFiltro(termoBusca, categoria, departamento, modalidade);
    }

    /**
     * Busca os detalhes completos de um evento pelo seu nome.
     * @param nome Nome do evento a ser buscado.
     * @return DTO com detalhes completos do evento, ou null se não encontrado.
     */
    public EventoDTO buscarDtoPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        return eventoService.buscarDtoPorNome(nome);
    }

    /**
     * Busca um evento pelo seu ID.
     * @param id ID do evento a ser buscado.
     * @return O evento encontrado, ou null se não encontrado.
     */
    public Evento buscarEventoPorId(Long id) {
        if (id == null) {
            return null;
        }
        return eventoService.buscarEventoPorId(id);
    }
}