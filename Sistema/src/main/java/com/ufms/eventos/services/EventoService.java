package com.ufms.eventos.services;

import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.UsuarioRepository;
import com.ufms.eventos.repository.OrganizadorRepository;
import com.ufms.eventos.dto.EventoDTO;

public class EventoService {
    private EventoRepository er;
    private AcaoRepository ar;
    private UsuarioRepository ur;
    private OrganizadorRepository or;


    public EventoService() {
        this.er = new EventoRepository();
        this.ar = new AcaoRepository();
        this.ur = new UsuarioRepository();
        this.or = new OrganizadorRepository();
    }

    public boolean addEvento(String nome, String data, String descricao, String local, String horarioInicio,
                         String horarioFim, Organizador organizador, String departamento, String contato,
                         String modalidade, String categoria, String imagem, String link, int capacidade) {
    this.or.addOrganizador(organizador);
    // Adiciona o evento ao repositório, incluindo o organizador
    return this.er.addEvento(nome, data, descricao, local, horarioInicio, horarioFim, organizador, departamento, contato,
                             modalidade, categoria, imagem, link, capacidade);
    }

    public boolean addEvento(EventoDTO eventoDTO, String organizador) {
        // Adiciona o organizador ao repositório, se necessário
        this.or.addOrganizador(new Organizador(organizador));
        // Adiciona o evento ao repositório, incluindo o organizador
        return this.er.addEvento(
            eventoDTO.getNome(),
            eventoDTO.getData(),
            eventoDTO.getDescricao(),
            eventoDTO.getLocal(),
            eventoDTO.getHorarioInicio(),
            eventoDTO.getHorarioFim(),
            new Organizador(organizador),
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

