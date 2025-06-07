package com.ufms.eventos.model;

import java.util.Objects;

import lombok.Data;

@Data
public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String telefone;

    public Usuario(String nome, String email, String senha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Usuario() {
        // Construtor padrão
    }

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
        this.email = null; // Email não é obrigatório nesse construtor
        this.telefone = null; // Telefone não é obrigatório nesse construtor

    }

    @Override
    public String toString() {
        return "Usuário{" +
                "nome='" + nome + '\'' +
                "telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        // Dois usuários são considerados iguais se seus nomes forem iguais.
        return Objects.equals(nome, usuario.nome);
    }

    @Override
    public int hashCode() {
        // O hashCode também deve ser baseado no campo usado no equals.
        return Objects.hash(nome);
    }
}