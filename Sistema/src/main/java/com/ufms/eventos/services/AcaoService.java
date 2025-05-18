package com.ufms.eventos.services;

import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.AcaoRepository;
import com.ufms.eventos.repository.EventoRepository;
import com.ufms.eventos.repository.OrganizadorRepository;
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

    public boolean addAcao(String nome, String data, String descricao, String local, String horarioInicio,
                      String horarioFim, Organizador organizador, String departamento, String contato,
                      String modalidade, String categoria, String imagem, String link, int capacidade) {
    organizadorRepository.addOrganizador(organizador);
    return acaoRepository.addAcao(
        new Acao(
            null, // evento, se necessário
            nome,
            java.time.LocalDate.parse(data),
            descricao,
            local,
            java.time.LocalTime.parse(horarioInicio),
            java.time.LocalTime.parse(horarioFim),
            organizador,
            departamento,
            contato,
            modalidade,
            categoria,
            imagem,
            link,
            capacidade
        )
    );
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
