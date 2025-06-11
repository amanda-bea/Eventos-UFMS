package com.ufms.eventos.controller;

import com.ufms.eventos.services.OrganizadorService;
import com.ufms.eventos.model.Organizador;


public class OrganizadorController {
    private OrganizadorService organizadorService;

    public OrganizadorController() {
        this.organizadorService = new OrganizadorService();
    }
    
    public Organizador buscarOrganizador(String nome) {
        return organizadorService.buscarPorNome(nome);
    }
    
    public boolean verificarOrganizador(String nome) {
        if (nome == null || nome.isEmpty()) {
            return false;
        }
        return organizadorService.verificarOrganizador(nome);
    }
    
    public boolean salvarOrganizador(Organizador organizador) {
        if (organizador == null || organizador.getNome() == null || organizador.getNome().isEmpty()) {
            return false;
        }
        return organizadorService.salvarOrganizador(organizador);
    }
    
    public boolean registrarOrganizador(Organizador organizador) {
        if (organizador == null || organizador.getNome() == null || organizador.getNome().isEmpty()) {
            return false;
        }
        
        // Verifica se já existe um organizador com este nome
        Organizador existente = buscarOrganizador(organizador.getNome());
        if (existente != null) {
            return false; // Nome já existe
        }
        
        return organizadorService.adicionarOrganizador(organizador);
    }
}