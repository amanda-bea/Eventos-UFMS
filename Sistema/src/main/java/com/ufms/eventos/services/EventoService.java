package com.ufms.eventos.services;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarEventoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.OrganizadorRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de Serviço que contém TODA a lógica de negócio para Eventos.
 * É utilizada tanto pelo EventoController quanto pelo AdminController.
 */
public class EventoService {
    private EventoRepository er;
    private OrganizadorRepository or;
    private AcaoRepository ar;

    public EventoService() {
        this.er = new EventoRepository();
        this.or = new OrganizadorRepository();
        this.ar = new AcaoRepository();
    }

    // ===================================================================
    // MÉTODOS PARA USUÁRIOS E ORGANIZADORES (Chamados pelo EventoController)
    // ===================================================================

    /**
     * Método principal para criar um evento. Recebe qualquer usuário e o trata
     * como o organizador do evento.
     */
    public boolean solicitarEventoComAcoes(EventoDTO eventoDTO, List<AcaoDTO> listaAcoesDTO, Usuario criadorDoEvento) {
        // Converte o Usuario em um objeto Organizador para o contexto do evento.
        Organizador organizadorDoEvento = new Organizador(
            criadorDoEvento.getNome(), criadorDoEvento.getEmail(),
            criadorDoEvento.getSenha(), criadorDoEvento.getTelefone()
        );
        // Em um sistema real, você salvaria/atualizaria este registro de organizador no banco.
        // or.salvar(organizadorDoEvento);

        Evento evento = new Evento(eventoDTO);
        evento.setStatus("Aguardando aprovação");
        evento.setOrganizador(organizadorDoEvento);
        
        if (er.getEvento(evento.getNome()) == null) {
            er.addEvento(evento);
        }

        for (AcaoDTO acaoDTO : listaAcoesDTO) {
            Acao acao = new Acao(acaoDTO);
            acao.setEvento(evento);
            acao.setStatus("Aguardando aprovação");
            ar.addAcao(acao);
        }
        return true;
    }

    /**
     * CORRIGIDO: Edita um evento. Recebe o usuário logado para verificar a permissão.
     */
    public boolean editarEvento(EditarEventoDTO dto, Usuario usuarioLogado) {
        Evento evento = er.getEvento(dto.getNomeEvento());
        
        // A verificação agora é muito mais limpa e correta
        if (evento != null && evento.getOrganizador().equals(usuarioLogado)) {
            evento.setDataInicio(LocalDate.parse(dto.getNovaDataInicio()));
            evento.setDataFim(LocalDate.parse(dto.getNovaDataFim()));
            evento.setLink(dto.getNovoLink());
            er.updateEvento(evento);
            return true;
        }
        return false;
    }
    
    /**
     * CORRIGIDO: Exclui uma solicitação de evento. Recebe o usuário logado para verificar a permissão.
     */
    public boolean excluirSolicitacaoEvento(String nomeEvento, Usuario usuarioLogado) {
        Evento evento = er.getEvento(nomeEvento);
        
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())
            && evento.getOrganizador().equals(usuarioLogado)) {
            return er.removeEvento(evento);
        }
        return false;
    }

    /**
     * CORRIGIDO: Busca eventos associados a um usuário específico.
     */
    public List<EventoMinDTO> buscarEventosPorUsuario(Usuario usuario) {
        if (usuario == null) return new ArrayList<>();

        return er.getEventos().stream()
                .filter(evento -> evento.getOrganizador() != null && evento.getOrganizador().equals(usuario))
                .map(EventoMinDTO::new)
                .collect(Collectors.toList());
    }

    // ===================================================================
    // MÉTODOS PARA ADMINS (Chamados pelo AdminController)
    // ===================================================================

    public List<EventoMinDTO> buscarEventosPorStatus(String status) {
        return er.getEventos().stream()
                .filter(e -> status.equalsIgnoreCase(e.getStatus()))
                .map(EventoMinDTO::new)
                .collect(Collectors.toList());
    }

    public boolean aprovarEvento(String nomeEvento) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            evento.setStatus("Ativo");
            er.updateEvento(evento);
            // ... lógica para aprovar ações filhas ...
            return true;
        }
        return false;
    }

    public boolean rejeitarEvento(String nomeEvento, String motivo) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            evento.setStatus("Rejeitado");
            evento.setMensagemRejeicao(motivo);
            er.updateEvento(evento);
            // ... lógica para rejeitar ações filhas ...
            return true;
        }
        return false;
    }

    // ===================================================================
    // MÉTODOS GERAIS
    // ===================================================================

    public List<EventoMinDTO> listarEventosAtivosMin() {
        return er.getEventos().stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .map(EventoMinDTO::new)
                .collect(Collectors.toList());
    }

    public EventoDTO buscarDtoPorId(Long id) {
        Evento evento = er.findById(id);
        if (evento == null) {
            throw new IllegalArgumentException("Evento com ID " + id + " não encontrado.");
        }
        return new EventoDTO(evento);
    }

    public List<EventoMinDTO> buscarEventosComFiltro(String termoBusca, Categoria categoria, Departamento departamento, String modalidade) {
    
        // Pega todos os eventos com status "Ativo" como base
        List<Evento> eventosFiltrados = this.listarEventosAtivos();
        
        // Aplica filtro de termo de busca
        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            String termoLowerCase = termoBusca.toLowerCase();
            eventosFiltrados = eventosFiltrados.stream()
                    .filter(e -> e.getNome().toLowerCase().contains(termoLowerCase) || 
                                e.getDescricao().toLowerCase().contains(termoLowerCase))
                    .collect(Collectors.toList());
        }

        // Aplica filtro de Categoria
        if (categoria != null) {
            eventosFiltrados = eventosFiltrados.stream()
                    .filter(e -> e.getCategoria() == categoria)
                    .collect(Collectors.toList());
        }

        // Aplica filtro de Departamento
        if (departamento != null) {
            eventosFiltrados = eventosFiltrados.stream()
                    .filter(e -> e.getDepartamento() == departamento)
                    .collect(Collectors.toList());
        }

        // Aplica filtro de Modalidade (lembre-se de adicionar o campo 'modalidade' na sua classe Evento)
        if (modalidade != null && !modalidade.isEmpty()) {
            eventosFiltrados = eventosFiltrados.stream()
                    // Mantém na lista apenas os eventos que possuem pelo menos uma ação com a modalidade desejada.
                    .filter(evento -> temAcaoComModalidade(evento, modalidade))
                    .collect(Collectors.toList());
        }

        // Converte a lista final de entidades para DTOs
        return eventosFiltrados.stream()
                .map(EventoMinDTO::new)
                .collect(Collectors.toList());
    }


    private List<Evento> listarEventosAtivos() {
        return er.getEventos().stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
    }

    private boolean temAcaoComModalidade(Evento evento, String modalidade) {
    // Busca em TODAS as ações do repositório
    return ar.getAcoes().stream()
            // Filtra as ações que pertencem ao evento em questão
            .filter(acao -> acao.getEvento().equals(evento))
            // Verifica se ALGUMA das ações filtradas tem a modalidade correta (ignorando maiúsculas/minúsculas)
            .anyMatch(acao -> modalidade.equalsIgnoreCase(acao.getModalidade()));
}

}