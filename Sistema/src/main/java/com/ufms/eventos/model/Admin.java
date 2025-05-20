package com.ufms.eventos.model;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) //tenho que ver oq é isso depois
public class Admin extends Usuario {
    private String cargo; // só pra exemplo

    public Admin(String nome, String email, String senha, String telefone) {
        super(nome, email, senha, telefone);
        this.cargo = "Admin"; // Definindo cargo como Admin por padrão
    }

    public void confirmarEvento(Evento evento) {
        // Exibir informações do evento
        System.out.println("Detalhes do evento:");
        System.out.println("Nome: " + evento.getNome());
        System.out.println("Data de Início: " + evento.getDataInicio());
        System.out.println("Data de Fim: " + evento.getDataFim());
        System.out.println("Descrição: " + evento.getDescricao());
        System.out.println("Organizador: " + evento.getOrganizador());
        System.out.println("Categoria: " + evento.getCategoria());
        System.out.println("Status Atual: " + evento.getStatus());
    
        // Confirmar o evento
        evento.setStatus("Ativo");
        System.out.println("O evento foi confirmado com sucesso!");
    }
    
    public void rejeitarEvento(Evento evento) {
        // Exibir informações do evento
        System.out.println("Detalhes do evento:");
        System.out.println("Nome: " + evento.getNome());
        System.out.println("Data de Início: " + evento.getDataInicio());
        System.out.println("Data de Fim: " + evento.getDataFim());
        System.out.println("Descrição: " + evento.getDescricao());
        System.out.println("Organizador: " + evento.getOrganizador());
        System.out.println("Categoria: " + evento.getCategoria());
        System.out.println("Status Atual: " + evento.getStatus());
    
        // Rejeitar o evento
        evento.setStatus("Rejeitado");
        System.out.println("O evento foi rejeitado.");
        // Perguntar o motivo da rejeição
        
        System.out.println("Motivo da rejeição: " + evento.getMensagemRejeicao());
    }


}
