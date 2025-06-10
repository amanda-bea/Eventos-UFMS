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
import com.ufms.eventos.repository.AcaoRepositoryJDBC;
import com.ufms.eventos.repository.EventoRepositoryJDBC;
import com.ufms.eventos.repository.OrganizadorRepositoryJDBC;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de Serviço que contém TODA a lógica de negócio para Eventos.
 * É utilizada tanto pelo EventoController quanto pelo AdminController.
 */
public class EventoService {
    private EventoRepositoryJDBC er;
    private AcaoRepositoryJDBC ar;
    private AcaoService acaoService;
    private OrganizadorRepositoryJDBC organizadorRepository;

    public EventoService() {
        this.er = new EventoRepositoryJDBC();
        this.ar = new AcaoRepositoryJDBC();
        this.acaoService = new AcaoService();
        this.organizadorRepository = new OrganizadorRepositoryJDBC();
    }

    // ===================================================================
    // MÉTODOS PARA USUÁRIOS E ORGANIZADORES (Chamados pelo EventoController)
    // ===================================================================

    /**
     * CORRIGIDO: Salva um evento e suas ações, e RETORNA as ações recém-criadas com seus IDs do banco.
     * @return Uma lista de objetos Acao persistidos, ou uma lista vazia se falhar.
     */
    public List<Acao> solicitarEventoComAcoes(EventoDTO eventoDTO, List<AcaoDTO> listaAcoesDTO, Usuario criadorDoEvento) {
        // Lógica para obter ou criar o Organizador
        Organizador organizador = organizadorRepository.getOrganizador(criadorDoEvento.getNome());
        if (organizador == null) {
            // Criar um novo organizador usando o construtor padrão e setters
            Organizador novoOrganizador = new Organizador();
            novoOrganizador.setNome(criadorDoEvento.getNome());
            novoOrganizador.setEmail(criadorDoEvento.getEmail());
            
            // Definir outros campos disponíveis do usuário que devem ser copiados para o organizador
            if (criadorDoEvento.getTelefone() != null) {
                novoOrganizador.setTelefone(criadorDoEvento.getTelefone());
            }
            
            // Salvar no repositório
            organizador = organizadorRepository.salvar(novoOrganizador);
        }

        Evento evento = new Evento(eventoDTO);
        evento.setOrganizador(organizador);
        evento.setStatus("Aguardando aprovação");
        
        // Salva o evento principal. O método 'salvar' deve popular o ID no objeto 'evento'.
        try {
            er.salvar(evento);
        } catch (Exception e) {
            System.err.println("Erro ao salvar o evento principal: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Retorna lista vazia em caso de falha
        }
        if (evento.getId() == null) {
            System.err.println("Falha ao salvar o evento principal no banco de dados.");
            return new ArrayList<>(); // Retorna lista vazia em caso de falha
        }

        List<Acao> acoesSalvas = new ArrayList<>();
        for (AcaoDTO acaoDTO : listaAcoesDTO) {
            Acao acao = new Acao(acaoDTO);
            acao.setEvento(evento); // Vincula ao evento recém-criado, que já tem ID
            acao.setStatus("Aguardando aprovação");
            
            // O método 'add' do AcaoRepository também deve popular o ID no objeto 'acao'.
            if (ar.addAcao(acao)) {
                acoesSalvas.add(acao);
            }
        }
        
        if (acoesSalvas.size() != listaAcoesDTO.size()){
            System.err.println("Falha ao salvar uma ou mais ações. Iniciando rollback...");
            deletarEventoCompleto(evento.getNome());
            return new ArrayList<>();
        }
        
        return acoesSalvas;
    }


    /**
     * Deleta um evento e todas as suas ações e configurações associadas (rollback).
     */
    public boolean deletarEventoCompleto(String nomeEvento) {
        try {
            Evento evento = buscarPorNome(nomeEvento);
            if (evento != null) {
                // Usar o método existente para buscar ações por evento ID
                List<Acao> acoes = ar.findByEventoId(evento.getId());
                for (Acao acao : acoes) {
                    
                    ar.delete(acao);
                }
                
                // Usar o método deletar em vez de delete
                er.deletar(evento.getId());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao deletar evento completo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca um evento pelo nome usando o repositório JDBC.
     */
    private Evento buscarPorNome(String nome) {
        try {
            List<Evento> eventos = er.listarTodos();
            return eventos.stream()
                .filter(e -> e.getNome().equals(nome))
                .findFirst().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Edita um evento. Recebe o usuário logado para verificar a permissão.
     */
    public boolean editarEvento(EditarEventoDTO dto, Usuario usuarioLogado) {
        try {
            Evento evento = buscarPorNome(dto.getNomeEvento());
            
            // A verificação agora é mais limpa e correta
            if (evento != null && evento.getOrganizador().getNome().equals(usuarioLogado.getNome())) {
                evento.setDataInicio(LocalDate.parse(dto.getNovaDataInicio()));
                evento.setDataFim(LocalDate.parse(dto.getNovaDataFim()));
                evento.setLink(dto.getNovoLink());
                er.atualizar(evento);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca DTO de evento por nome usando JDBC.
     */
    public EventoDTO buscarDtoPorNome(String nome) {
        try {
            Evento evento = buscarPorNome(nome);
            if (evento == null) {
                throw new IllegalArgumentException("Evento com nome '" + nome + "' não encontrado.");
            }
            return new EventoDTO(evento);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao buscar evento: " + e.getMessage());
        }
    }
    
    /**
     * Exclui uma solicitação de evento. Recebe o usuário logado para verificar a permissão.
     */
    public boolean excluirSolicitacaoEvento(String nomeEvento, Usuario usuarioLogado) {
        try {
            Evento evento = buscarPorNome(nomeEvento);
            
            if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())
                && evento.getOrganizador().getNome().equals(usuarioLogado.getNome())) {
                er.deletar(evento.getId());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<EventoMinDTO> buscarEventosPorUsuario(Usuario usuario) {
        List<EventoMinDTO> eventosMiniDTO = new ArrayList<>();
        try {
            List<Evento> eventos = er.buscarPorOrganizador(usuario.getNome());
            
            for (Evento evento : eventos) {
                EventoMinDTO dto = new EventoMinDTO();
                // IMPORTANTE: Certifique-se de definir o ID!
                dto.setId(evento.getId());  // Aqui está a correção principal
                dto.setNome(evento.getNome());
                dto.setStatus(evento.getStatus());
                dto.setCategoria(evento.getCategoria().toString());
                dto.setDataInicio(evento.getDataInicio().toString());
                dto.setImagem(evento.getImagem());
                
                // Verificação adicional para garantir que o ID está sendo definido
                if (dto.getId() == null) {
                    System.err.println("ALERTA: O evento " + dto.getNome() + 
                                    " tem ID nulo no objeto original! ID no banco: " + evento.getId());
                }
                
                eventosMiniDTO.add(dto);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar eventos do usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return eventosMiniDTO;
    }

    // ===================================================================
    // MÉTODOS PARA ADMINS (Chamados pelo AdminController)
    // ===================================================================

    /**
     * Busca eventos por status usando JDBC.
     */
    public List<EventoMinDTO> buscarEventosPorStatus(String status) {
        try {
            List<Evento> eventos = er.findByStatus(status);
            return eventos.stream()
                    .map(EventoMinDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Aprova um evento utilizando JDBC.
     */
    public boolean aprovarEvento(String nomeEvento) {
        try {
            Evento evento = buscarPorNome(nomeEvento);
            if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
                evento.setStatus("Ativo");
                er.atualizar(evento);
                
                // Aprovar ações filhas
                List<Acao> acoesDoEvento = ar.findByEventoId(evento.getId());
                for (Acao acao : acoesDoEvento) {
                    acao.setStatus("Ativo");
                    ar.updateAcao(acao);
                }
                
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rejeita um evento utilizando JDBC.
     */
    public boolean rejeitarEvento(String nomeEvento, String motivo) {
        try {
            Evento evento = buscarPorNome(nomeEvento);
            if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
                evento.setStatus("Rejeitado");
                evento.setMensagemRejeicao(motivo);
                er.atualizar(evento);
                
                // Rejeitar ações filhas
                List<Acao> acoesDoEvento = ar.findByEventoId(evento.getId());
                for (Acao acao : acoesDoEvento) {
                    acao.setStatus("Rejeitado");
                    acao.setMensagemRejeicao("Evento principal rejeitado: " + motivo);
                    ar.updateAcao(acao);
                }
                
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================================================================
    // MÉTODOS GERAIS
    // ===================================================================

    /**
     * Lista eventos ativos em formato resumido usando JDBC.
     */
    public List<EventoMinDTO> listarEventosAtivosMin() {
        try {
            List<Evento> eventos = er.findByStatus("Ativo");
            return eventos.stream()
                    .map(EventoMinDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Busca DTO de evento por ID usando JDBC.
     */
    public EventoDTO buscarDtoPorId(Long id) {
        try {
            Evento evento = er.buscarPorId(id);
            if (evento == null) {
                throw new IllegalArgumentException("Evento com ID " + id + " não encontrado.");
            }
            return new EventoDTO(evento);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Erro ao buscar evento: " + e.getMessage());
        }
    }

    /**
     * Busca eventos com filtros combinados usando JDBC.
     */
    public List<EventoMinDTO> buscarEventosComFiltro(String termoBusca, Categoria categoria, 
                                                     Departamento departamento, String modalidade) {
        try {
            // Base: eventos ativos 
            List<Evento> eventosAtivos = er.findByStatus("Ativo");
            List<Evento> eventosFiltrados = new ArrayList<>(eventosAtivos);
            
            // Filtra por termo de busca
            if (termoBusca != null && !termoBusca.trim().isEmpty()) {
                String termoLowerCase = termoBusca.toLowerCase();
                eventosFiltrados = eventosFiltrados.stream()
                        .filter(e -> (e.getNome() != null && e.getNome().toLowerCase().contains(termoLowerCase)) || 
                                     (e.getDescricao() != null && e.getDescricao().toLowerCase().contains(termoLowerCase)))
                        .collect(Collectors.toList());
            }
    
            // Filtra por categoria
            if (categoria != null) {
                eventosFiltrados = eventosFiltrados.stream()
                        .filter(e -> e.getCategoria() == categoria)
                        .collect(Collectors.toList());
            }
    
            // Filtra por departamento
            if (departamento != null) {
                eventosFiltrados = eventosFiltrados.stream()
                        .filter(e -> e.getDepartamento() == departamento)
                        .collect(Collectors.toList());
            }
    
            // Filtra por modalidade das ações
            if (modalidade != null && !modalidade.isEmpty()) {
                eventosFiltrados = eventosFiltrados.stream()
                        .filter(evento -> temAcaoComModalidade(evento, modalidade))
                        .collect(Collectors.toList());
            }
    
            // Retorna os DTOs
            return eventosFiltrados.stream()
                    .map(EventoMinDTO::new)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Verifica se um evento tem alguma ação com a modalidade especificada.
     */
    private boolean temAcaoComModalidade(Evento evento, String modalidade) {
        List<Acao> acoesDoEvento = ar.findByEventoId(evento.getId());
        return acoesDoEvento.stream()
                .anyMatch(acao -> modalidade.equalsIgnoreCase(acao.getModalidade()));
    }

    /**
     * Busca evento por ID usando JDBC.
     */
    public Evento buscarEventoPorId(Long id) {
        try {
            return er.buscarPorId(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Atualiza status de eventos e ações baseado na data atual, usando JDBC.
     */
    public void atualizarStatusDeEventosEAcoes() {
        try {
            // 1. Inativa todas as ações que já passaram da data
            acaoService.atualizarAcoesExpiradas();
    
            // 2. Pega todos os eventos que ainda estão "Ativos"
            List<Evento> eventosAtivos = er.findByStatus("Ativo");
    
            // 3. Para cada evento ativo, verifica o status de suas ações
            for (Evento evento : eventosAtivos) {
                // Pega todas as ações associadas a este evento
                List<Acao> acoesDoEvento = ar.findByEventoId(evento.getId());
                
                if (acoesDoEvento.isEmpty()) {
                    continue; // Pula eventos que não têm ações
                }
    
                // Verifica se TODAS as ações deste evento estão inativas ou canceladas
                boolean todasAcoesInativas = acoesDoEvento.stream()
                    .allMatch(a -> "Inativo".equalsIgnoreCase(a.getStatus()) || 
                                  "Cancelado".equalsIgnoreCase(a.getStatus()));
    
                if (todasAcoesInativas) {
                    System.out.println("Inativando evento '" + evento.getNome() + "' pois todas as suas ações expiraram.");
                    evento.setStatus("Inativo");
                    er.atualizar(evento);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao atualizar status de eventos: " + e.getMessage());
        }
    }
}