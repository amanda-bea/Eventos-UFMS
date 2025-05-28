//CAMADA QUE FAZ A INTERAÇÃO ENTRE O CONTROLLER E O REPOSITORY
//CAMADA DE SERVIÇO
//FAZ A REQUISIÇÃO DOS DADOS DO BANCO DE DADOS COM O REPOSITORY
//CONTÉM A LÓGICA/CASOS DE USO

package com.ufms.eventos.services;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.OrganizadorRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.HashSet;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EditarEventoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.EventoMinDTO;

public class EventoService {
    private EventoRepository er;
    private OrganizadorRepository or;
    private AcaoRepository ar;

    public EventoService() {
        this.er = new EventoRepository();
        this.or = new OrganizadorRepository();
        this.ar = new AcaoRepository();
    }

    public List<EventoDTO> listarEventosAtivos() {
        HashSet<Evento> eventos = er.getEventos();
        return eventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .map(EventoDTO::new)
                .collect(Collectors.toList());
    }

    public List<EventoMinDTO> listarEventosAtivosMin() {
        return er.getEventos().stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .map(EventoMinDTO::new)
                .collect(Collectors.toList());
    }

    //public EventoDTO buscarEventoPorId(Long id) {
    //    Evento evento = er.getEventos().stream()
    //            .filter(e -> e.getId().equals(id))
    //            .findFirst()
    //            .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));
    //
    //    return new EventoDTO(evento);
    //}

    //Organizador deve ser recebido pelo controller obtendo o usuário logado
    public boolean solicitarEvento(EventoDTO eventoDTO, Organizador organizador) {
        Evento evento = new Evento(eventoDTO);
        evento.setStatus("Aguardando aprovação");
        evento.setOrganizador(organizador);
        evento.setMensagemRejeicao(null);

        // Se o evento ainda não existe no repositório, adiciona
        if (er.getEvento(evento.getNome()) == null) {
            er.addEvento(evento);
        }

        this.or.addOrganizador(organizador);

        // Retorna true se o evento foi adicionado ou já existia
        return true;
    }

    public boolean excluirSolicitacaoEvento(String nomeEvento, Organizador organizador) {
        Evento evento = er.getEvento(nomeEvento);
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())
            && evento.getOrganizador().equals(organizador)) {
            return er.removeEvento(evento);
        }
        return false;
    }

    /* 
    // Método utilitário para listar categorias fixas
    public List<String> listarCategorias() {
        return Arrays.stream(Categoria.values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
    }

    // Método utilitário para listar departamentos fixos
    public List<String> listarDepartamentos() {
        return Arrays.stream(Departamento.values())
                     .map(Enum::name)
                     .collect(Collectors.toList());
    }
    */

    // Depois colocar esse metodo para rodar ao abrir o sistema
    public void atualizarEventosExpirados() {
        LocalDate hoje = LocalDate.now();
        Set<Evento> eventos = er.getEventos();

        for (Evento evento : eventos) {
            if (evento.getDataFim().isBefore(hoje) && !"Inativo".equalsIgnoreCase(evento.getStatus())) {
                evento.setStatus("Inativo");
                er.updateEvento(evento); // Atualiza no repositório em memória
            }
        }
    }

    public boolean editarEvento(EditarEventoDTO dto, Organizador organizador) {
        Evento evento = er.getEvento(dto.getNomeEvento());
        if (evento != null && evento.getOrganizador().equals(organizador)) {
            evento.setDataInicio(LocalDate.parse(dto.getNovaDataInicio()));
            evento.setDataFim(LocalDate.parse(dto.getNovaDataFim()));
            evento.setLink(dto.getNovoLink());
            er.updateEvento(evento);
            return true;
        }
        return false;
    }

    public boolean cancelarEvento(String nomeEvento, String motivo) { //ver depois
        Evento evento = er.getEvento(nomeEvento);
        if (evento == null) {
            throw new RuntimeException("Evento não encontrado");
        }
        evento.setStatus("Cancelado");
        evento.setMensagemRejeicao(null);
        return true;
    }

    public boolean solicitarEventoComAcoes(EventoDTO eventoDTO, List<AcaoDTO> listaAcoesDTO, Organizador organizador) {
        Evento evento = new Evento(eventoDTO);
        evento.setStatus("Aguardando aprovação");
        evento.setOrganizador(organizador);
        evento.setMensagemRejeicao(null);

        // Adiciona o evento se ainda não existir
        if (er.getEvento(evento.getNome()) == null) {
           er.addEvento(evento);
        }

        boolean sucesso = true;
        for (AcaoDTO acaoDTO : listaAcoesDTO) {
            Acao acao = new Acao(acaoDTO);
            acao.setEvento(evento);
            acao.setStatus("Aguardando aprovação");
            acao.setMensagemRejeicao(null);
            boolean adicionada = ar.addAcao(acao);
            if (!adicionada) {
                sucesso = false; // Se alguma ação não for adicionada, marca como falso
            }
        }
        return sucesso;
    }

}

