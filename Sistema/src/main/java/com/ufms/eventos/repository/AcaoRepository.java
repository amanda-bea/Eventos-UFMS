package com.ufms.eventos.repository;

import java.util.HashSet;
import com.ufms.eventos.model.Acao;

import java.util.Iterator;

public class AcaoRepository {
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

    public Acao geAcao(String nome) { // mudar ideia depois
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
}
