package com.ufms.eventos.model;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class FormularioInscricao {
    // Atributos padrão
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private Map<String, String> camposExtras = new HashMap<>(); // Campos dinâmicos do formulário

    public FormularioInscricao(String nome, String email, String telefone, String cpf) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    public FormularioInscricao() {
        // Construtor padrão
    }

    // Método para adicionar campos dinâmicos ao formulário
    public void adicionarCampo(String campo, String valor) {
        camposExtras.put(campo, valor);
        System.out.println("Campo adicionado: " + campo + " com valor: " + valor);
    }

    // Método para remover campos dinâmicos do formulário
    public void removerCampo(String campo) {
        if (camposExtras.containsKey(campo)) {
            camposExtras.remove(campo);
            System.out.println("Campo removido: " + campo);
        } else {
            System.out.println("Campo não encontrado: " + campo);
        }
    }

    // Método para exibir todos os campos do formulário
    public void exibirCampos() {
        System.out.println("Campos padrão:");
        System.out.println("Nome: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Telefone: " + telefone);
        System.out.println("CPF: " + cpf);

        System.out.println("Campos extras:");
        camposExtras.forEach((campo, valor) -> System.out.println(campo + ": " + valor));
    }
}