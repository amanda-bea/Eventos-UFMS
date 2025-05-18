//CAMADA QUE FAZ A INTERAÇÃO ENTRE O CONTROLLER E O REPOSITORY
//CAMADA DE SERVIÇO
//FAZ A REQUISIÇÃO DOS DADOS DO BANCO DE DADOS COM O REPOSITORY
//CONTÉM A LÓGICA/CASOS DE USO

package com.ufms.eventos.services;

import com.ufms.eventos.model.Evento;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.OrganizadorRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

import com.ufms.eventos.dto.EventoDTO;

public class EventoService {
    private EventoRepository er;
    private OrganizadorRepository or;


    public EventoService() {
        this.er = new EventoRepository();
        this.or = new OrganizadorRepository();
    }

    //public boolean addEvento(EventoDTO eventoDTO, String organizador) {
    //    // Adiciona o organizador ao repositório, se necessário
    //    this.or.addOrganizador(new Organizador(organizador));
    //    // Adiciona o evento ao repositório, incluindo o organizador
    //    return this.er.addEvento(
    //        eventoDTO.getNome(),
    //        eventoDTO.getData(),
    //        eventoDTO.getDescricao(),
    //        eventoDTO.getLocal(),
    //        eventoDTO.getHorarioInicio(),
    //        eventoDTO.getHorarioFim(),
    //        new Organizador(organizador),
    //        eventoDTO.getDepartamento(),
    //        eventoDTO.getContato(),
    //        eventoDTO.getModalidade(),
    //        eventoDTO.getCategoria(),
    //        eventoDTO.getImagem(),
    //        eventoDTO.getLink(),
    //        eventoDTO.getCapacidade()
    //    );
    //}

    // listar todos os eventios na tela inicial
    public List<EventoDTO> buscarEventosAtivos() {
        HashSet<Evento> eventos = er.getEventos();

        return eventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .map(EventoDTO::new)
                .collect(Collectors.toList());
    }

    public boolean solicitarEvento(EventoDTO eventoDTO, Organizador organizador) {
    // Adiciona o organizador ao repositório, se necessário
    this.or.addOrganizador(organizador);

    // Converte os campos de String para os tipos corretos
    java.time.LocalDate data = java.time.LocalDate.parse(eventoDTO.getData());
    java.time.LocalTime horarioInicio = java.time.LocalTime.parse(eventoDTO.getHorarioInicio());
    java.time.LocalTime horarioFim = java.time.LocalTime.parse(eventoDTO.getHorarioFim());

    // Adiciona o evento ao repositório, incluindo o organizador
    return this.er.addEvento(
        eventoDTO.getNome(),
        data,
        eventoDTO.getDescricao(),
        eventoDTO.getLocal(),
        horarioInicio,
        horarioFim,
        organizador,
        eventoDTO.getDepartamento(),
        eventoDTO.getContato(),
        eventoDTO.getModalidade(),
        eventoDTO.getCategoria(),
        eventoDTO.getImagem(),
        eventoDTO.getLink(),
        eventoDTO.getCapacidade()
    );
    }

}

