package com.ufms.eventos.model;

import java.util.Objects;

import lombok.Data;

@Data
public class Usuario {
    private Integer id; // Adicionado id
    private String nome;
    private String email;
    private String senha;
    private String telefone; // Formato: telefone ou e-mail

    public Usuario(String nome, String email, String senha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Usuario() {
        // Construtor padrão
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
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}