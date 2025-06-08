package com.ufms.eventos.repository;

import java.util.HashSet;
import com.ufms.eventos.model.Organizador;

public class OrganizadorRepository {
    private HashSet<Organizador> organizadores;

    public OrganizadorRepository() {
        this.organizadores = new HashSet<Organizador>();
    }

    public HashSet<Organizador> getOrganizadores() {
        return new HashSet<Organizador>(this.organizadores); //retorna uma cópia do conjunto de organizadores
    }

    public boolean addOrganizador(Organizador organizador) {
        return this.organizadores.add(organizador);
    }

    public boolean removeOrganizador(Organizador organizador) {
        return this.organizadores.remove(organizador);
    }

    public Organizador getOrganizador(String nome) { // mudar ideia depois
        for (Organizador o : this.organizadores) {
            if (o.getNome().equals(nome)) {
                return o;
            }
        }
        return null;
    }

    /**
     * Salva um organizador no repositório. Se já existir um organizador com o mesmo nome,
     * ele será atualizado com os novos dados. Caso contrário, será adicionado como novo.
     * 
     * @param organizador O organizador a ser salvo
     * @return O organizador salvo
     */
    public Organizador salvar(Organizador organizador) {
        if (organizador == null) {
            return null;
        }
        
        // Busca se já existe um organizador com o mesmo nome
        Organizador organizadorExistente = getOrganizador(organizador.getNome());
        
        if (organizadorExistente != null) {
            // Se existir, remove para atualizar
            this.organizadores.remove(organizadorExistente);
        }
        
        // Adiciona o organizador (novo ou atualizado)
        this.organizadores.add(organizador);
        return organizador;
    }


}
