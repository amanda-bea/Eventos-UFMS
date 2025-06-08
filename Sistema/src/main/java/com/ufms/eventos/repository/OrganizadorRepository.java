package com.ufms.eventos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import com.ufms.eventos.model.Organizador;

public class OrganizadorRepository implements IOrganizadorRepository {
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
    
    /**
     * Busca um organizador pelo ID.
     * @param id O ID do organizador a ser buscado.
     * @return O organizador encontrado, ou null se não encontrado.
     */
    @Override
    public Organizador findById(Long id) {
        if (id == null) {
            return null;
        }
        
        for (Organizador organizador : organizadores) {
            if (id.equals(organizador.getId())) {
                return organizador;
            }
        }
        
        return null;
    }
    
    /**
     * Converte um ResultSet para um objeto Organizador.
     * Este método é implementado para compatibilidade com a interface,
     * mas não é utilizado na implementação em memória.
     * 
     * @param rs O ResultSet contendo os dados do organizador
     * @return O objeto Organizador criado
     * @throws SQLException Se ocorrer um erro ao acessar o ResultSet
     */
    @Override
    public Organizador mapResultSetToOrganizador(ResultSet rs) throws SQLException {
        // Este método não é utilizado na implementação em memória
        // mas é implementado para compatibilidade com a interface
        throw new UnsupportedOperationException("Método não suportado nesta implementação");
    }
}
