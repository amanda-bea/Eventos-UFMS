package com.ufms.eventos.services;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.repository.OrganizadorRepositoryJDBC;

/**
 * Classe de serviço para operações relacionadas a organizadores.
 */
public class OrganizadorService {

    private final OrganizadorRepositoryJDBC organizadorRepository;

    public OrganizadorService() {
        this.organizadorRepository = new OrganizadorRepositoryJDBC();
    }
    
    /**
     * Busca um organizador pelo nome.
     * @param nome O nome do organizador.
     * @return O organizador encontrado ou null se não existir.
     */
    public Organizador buscarPorNome(String nome) {
        return organizadorRepository.getOrganizador(nome);
    }
    
    /**
     * Salva ou atualiza um organizador.
     * @param organizador O organizador a ser salvo.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean salvarOrganizador(Organizador organizador) {
        if (organizador == null) {
            return false;
        }
        return organizadorRepository.salvar(organizador) != null;
    }
    /**
     * Verifica se um usuário é organizador pelo nome.
     * @param nome O nome do usuário.
     * @return true se for organizador, false caso contrário.
     */
    public boolean verificarOrganizador(String nome) {
        return organizadorRepository.verificarOrganizador(nome);
    }
    
    /**
     * Adiciona um novo organizador.
     * @param organizador O organizador a ser adicionado.
     * @return true se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean adicionarOrganizador(Organizador organizador) {
        if (organizador == null) {
            return false;
        }
        return organizadorRepository.addOrganizador(organizador);
    }
}