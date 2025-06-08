package com.ufms.eventos.repository;

import java.util.ArrayList;
import java.util.HashSet;
import com.ufms.eventos.model.Acao;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class AcaoRepository implements IAcaoRepository {
    private HashSet<Acao> acoes;

    public AcaoRepository() {
        this.acoes = new HashSet<Acao>();
    }

    public HashSet<Acao> getAcoes(){
        return new HashSet<Acao>(this.acoes);
    }

    public boolean addAcao(Acao acao){
        return this.acoes.add(acao);
    }

    public boolean removeAcao(Acao acao){
        return this.acoes.remove(acao);
    }

    public boolean updateAcao(Acao acao){ ///atualizar regra depois, apenas exemplo
        if(this.acoes.contains(acao)){
            this.acoes.remove(acao);
            this.acoes.add(acao);
            return true;
        }
        return false;
    }

    public Acao getAcao(String nome) { // mudar ideia depois
        Iterator<Acao> iterator = this.acoes.iterator();

        Acao acao = null;
        while (iterator.hasNext()) {
            Acao a = iterator.next();
            if (a.getNome().equals(nome)) {
                acao = a;
                break;
            }
        }
        return acao;
    }

    public boolean deleteAcao(String nome) {
        Iterator<Acao> iterator = this.acoes.iterator();
        while (iterator.hasNext()) {
            Acao acao = iterator.next();
            if (acao.getNome().equals(nome)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public Acao buscarPorNome(String nome) {
        for (Acao acao : acoes) {
            if (acao.getNome().equalsIgnoreCase(nome)) {
                return acao;
            }
        }
        return null;
    }

    public List<Acao> findByEventoId(Long eventoId) {
        if (eventoId == null) {
            return new ArrayList<>(); // Retorna lista vazia se o ID for nulo
        }
        
        return this.acoes.stream()
                // Filtra as ações, mantendo apenas aquelas cujo evento associado tem o ID correto.
                .filter(acao -> acao.getEvento() != null && eventoId.equals(acao.getEvento().getId()))
                .collect(Collectors.toList());
    }

    public Acao findById(Long id) {
        if (id == null) {
            return null;
        }
        
        for (Acao acao : acoes) {
            if (id.equals(acao.getId())) {
                return acao;
            }
        }
        
        return null;
    }
    
    // Método adicional para compatibilidade com AcaoRepositoryJDBC
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        Iterator<Acao> iterator = this.acoes.iterator();
        while (iterator.hasNext()) {
            Acao acao = iterator.next();
            if (id.equals(acao.getId())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
