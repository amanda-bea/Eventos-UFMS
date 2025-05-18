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

    public boolean addOrganizador(String nome) {
        return this.organizadores.add(new Organizador(nome));
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



}
