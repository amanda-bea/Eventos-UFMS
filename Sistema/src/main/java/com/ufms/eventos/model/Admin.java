package com.ufms.eventos.model;
import lombok.Data;

@Data
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
        System.out.println("Data: " + evento.getData());
        System.out.println("Descrição: " + evento.getDescricao());
        System.out.println("Local: " + evento.getLocal());
        System.out.println("Horário de Início: " + evento.getHorarioInicio());
        System.out.println("Horário de Fim: " + evento.getHorarioFim());
        System.out.println("Organizador: " + evento.getOrganizador());
        System.out.println("Contato: " + evento.getContato());
        System.out.println("Modalidade: " + evento.getModalidade());
        System.out.println("Categoria: " + evento.getCategoria());
        System.out.println("Capacidade: " + evento.getCapacidade());
        System.out.println("Status Atual: " + evento.getStatus());
    
        // Confirmar o evento
        evento.setStatus("Ativo");
        System.out.println("O evento foi confirmado com sucesso!");
    }
    
    public void rejeitarEvento(Evento evento) {
        // Exibir informações do evento
        System.out.println("Detalhes do evento:");
        System.out.println("Nome: " + evento.getNome());
        System.out.println("Data: " + evento.getData());
        System.out.println("Descrição: " + evento.getDescricao());
        System.out.println("Local: " + evento.getLocal());
        System.out.println("Horário de Início: " + evento.getHorarioInicio());
        System.out.println("Horário de Fim: " + evento.getHorarioFim());
        System.out.println("Organizador: " + evento.getOrganizador());
        System.out.println("Contato: " + evento.getContato());
        System.out.println("Modalidade: " + evento.getModalidade());
        System.out.println("Categoria: " + evento.getCategoria());
        System.out.println("Capacidade: " + evento.getCapacidade());
        System.out.println("Status Atual: " + evento.getStatus());
    
        // Rejeitar o evento
        evento.setStatus("Rejeitado");
        System.out.println("O evento foi rejeitado.");
        //talvez colocar um motivo de rejeição aqui para ser enviado ao organizador
    }


}
