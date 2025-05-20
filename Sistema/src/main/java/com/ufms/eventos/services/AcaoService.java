package com.ufms.eventos.services;

import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.OrganizadorRepository;
import com.ufms.eventos.model.Evento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;

public class AcaoService {
    private AcaoRepository acaoRepository;
    private EventoRepository eventoRepository;
    private OrganizadorRepository or;

    public AcaoService() {
        this.acaoRepository = new AcaoRepository();
        this.eventoRepository = new EventoRepository();
        this.or = new OrganizadorRepository();
    }

    public boolean addAcao(Acao acao) {
        // Adiciona a ação ao repositório
        return acaoRepository.addAcao(acao);
    }

    public boolean addAcao(Evento evento, String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio,
            LocalTime horarioFim, Organizador organizador, Departamento departamento, String contato, String modalidade,
            Categoria categoria, String imagem, String link, int capacidade, String status, String mensagemRejeicao) {
        // Adiciona a ação ao repositório
        Acao acao = new Acao(evento, nome, data, descricao, local, horarioInicio, horarioFim, organizador, departamento,
                             contato, modalidade, categoria, imagem, link, capacidade, status, mensagemRejeicao);
        return acaoRepository.addAcao(acao);
    }

    public boolean addAcao(AcaoDTO acaoDTO, Organizador organizador, EventoDTO eventoDTO) {
        this.or.addOrganizador(organizador);

        // Converte os campos do DTO para os tipos corretos
        LocalDate data = LocalDate.parse(acaoDTO.getData());
        LocalTime horarioInicio = LocalTime.parse(acaoDTO.getHorarioInicio());
        LocalTime horarioFim = LocalTime.parse(acaoDTO.getHorarioFim());

        Departamento departamento = Departamento.valueOf(acaoDTO.getDepartamento());
        Categoria categoria = Categoria.valueOf(acaoDTO.getCategoria());

        String status = "Aguardando aprovação";
        String mensagemRejeicao = null;

        // Converte EventoDTO para Evento
        Evento evento = null;
        if (eventoDTO != null) {
            LocalDate dataInicio = LocalDate.parse(eventoDTO.getDataInicio());
            LocalDate dataFim = LocalDate.parse(eventoDTO.getDataFim());
            Departamento depEvento = Departamento.valueOf(eventoDTO.getDepartamento());
            Categoria catEvento = Categoria.valueOf(eventoDTO.getCategoria());

            evento = new Evento(
                eventoDTO.getNome(),
                dataInicio,
                dataFim,
                eventoDTO.getDescricao(),
                organizador,
                depEvento,
                catEvento,
                eventoDTO.getImagem(),
                eventoDTO.getLink(),
                status,
                mensagemRejeicao
            );

            // Se o evento ainda não existe no repositório, adiciona
            if (eventoRepository.getEvento(evento.getNome()) == null) {
                eventoRepository.addEvento(
                    evento.getNome(),
                    evento.getDataInicio(),
                    evento.getDataFim(),
                    evento.getDescricao(),
                    evento.getOrganizador(),
                    evento.getDepartamento(),
                    evento.getCategoria(),
                    evento.getImagem(),
                    evento.getLink(),
                    evento.getStatus(),
                    evento.getMensagemRejeicao()
                );
            }
        }

        Acao acao = new Acao(
            evento,
            acaoDTO.getNome(),
            data,
            acaoDTO.getDescricao(),
            acaoDTO.getLocal(),
            horarioInicio,
            horarioFim,
            organizador,
            departamento,
            acaoDTO.getContato(),
            acaoDTO.getModalidade(),
            categoria,
            acaoDTO.getImagem(),
            acaoDTO.getLink(),
            Integer.parseInt(acaoDTO.getCapacidade()),
            status,
            mensagemRejeicao
        );
        return acaoRepository.addAcao(acao);
    }

    public HashSet<Acao> getAcoes() {
        // Retorna todas as ações do repositório
        return acaoRepository.getAcoes();
    }

    public boolean updateAcao(Acao acao) {
        // Atualiza a ação no repositório
        return acaoRepository.updateAcao(acao);
    }

    public boolean deleteAcao(String nome) {
        // Deleta a ação do repositório
        return acaoRepository.deleteAcao(nome);
    }

    public Acao getAcao(String nome) {
        // Retorna a ação pelo nome
        return acaoRepository.getAcao(nome);
    }

}
