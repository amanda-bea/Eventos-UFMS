package com.ufms.eventos.controller;

import com.ufms.eventos.services.OrganizadorService;
import com.ufms.eventos.model.Organizador;

/**
 * Controller para operações relacionadas a organizadores.
 */
public class OrganizadorController {
    private OrganizadorService organizadorService;

    public OrganizadorController() {
        this.organizadorService = new OrganizadorService();
    }

    /**
     * Construtor para injeção de dependência (facilita testes).
     */
    public OrganizadorController(OrganizadorService organizadorService) {
        this.organizadorService = organizadorService;
    }
    
    /**
     * Busca um organizador pelo nome.
     * @param nome O nome do organizador.
     * @return O organizador encontrado ou null se não existir.
     */
    public Organizador buscarOrganizador(String nome) {
        return organizadorService.buscarPorNome(nome);
    }
    
    /**
     * Verifica se um usuário é organizador.
     * @param nome O nome do usuário.
     * @return true se for organizador, false caso contrário.
     */
    public boolean verificarOrganizador(String nome) {
        if (nome == null || nome.isEmpty()) {
            return false;
        }
        return organizadorService.verificarOrganizador(nome);
    }
    
    /**
     * Salva ou atualiza um organizador.
     * @param organizador Os dados do organizador.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean salvarOrganizador(Organizador organizador) {
        if (organizador == null || organizador.getNome() == null || organizador.getNome().isEmpty()) {
            return false;
        }
        return organizadorService.salvarOrganizador(organizador);
    }
    
    /**
     * Registra um novo organizador.
     * @param organizador Os dados do organizador.
     * @return true se o registro foi bem-sucedido, false caso contrário.
     */
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