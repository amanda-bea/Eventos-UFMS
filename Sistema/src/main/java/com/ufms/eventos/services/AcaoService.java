package com.ufms.eventos.services;

import com.ufms.eventos.model.Acao;
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
    private OrganizadorRepository organizadorRepository;

    public AcaoService() {
        this.acaoRepository = new AcaoRepository();
        this.eventoRepository = new EventoRepository();
        this.organizadorRepository = new OrganizadorRepository();
    }

    public boolean addAcao(Acao acao) {
        // Adiciona a ação ao repositório
        return acaoRepository.addAcao(acao);
    }

    public boolean addAcao(Evento evento, String nome, LocalDate data, String descricao, String local, LocalTime horarioInicio,
            LocalTime horarioFim, Organizador organizador, String departamento, String contato, String modalidade,
            String categoria, String imagem, String link, int capacidade, String status, String mensagemRejeicao) {
        // Adiciona a ação ao repositório
        Acao acao = new Acao(evento, nome, data, descricao, local, horarioInicio, horarioFim, organizador, departamento,
                             contato, modalidade, categoria, imagem, link, capacidade, status, mensagemRejeicao);
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
