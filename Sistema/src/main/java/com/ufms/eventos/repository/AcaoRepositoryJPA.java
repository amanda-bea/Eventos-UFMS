/*
package com.ufms.eventos.repository;

import java.util.HashSet;
import com.ufms.eventos.model.Acao;

import java.util.Iterator;


public class AcaoRepositoryJPA implements AcaoRepository {
    private EntityManager em;

    public AcaoRepositoryJPA(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean addAcao(Acao acao) {
        em.getTransaction().begin();
        em.persist(acao);
        em.getTransaction().commit();
        return true;
    }

    @Override
    public boolean removeAcao(Acao acao) {
        em.getTransaction().begin();
        Acao a = em.merge(acao); // precisa estar gerenciado
        em.remove(a);
        em.getTransaction().commit();
        return true;
    }

    @Override
    public boolean updateAcao(Acao acao) {
        em.getTransaction().begin();
        em.merge(acao);
        em.getTransaction().commit();
        return true;
    }

    @Override
    public Acao getAcao(String nome) {
        String jpql = "SELECT a FROM Acao a WHERE a.nome = :nome";
        return em.createQuery(jpql, Acao.class)
                 .setParameter("nome", nome)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }

    @Override
    public boolean deleteAcao(String nome) {
        Acao acao = getAcao(nome);
        if (acao != null) {
            return removeAcao(acao);
        }
        return false;
    }

    @Override
    public Set<Acao> getAcoes() {
        return new HashSet<>(em.createQuery("SELECT a FROM Acao a", Acao.class).getResultList());
    }
}
*/
