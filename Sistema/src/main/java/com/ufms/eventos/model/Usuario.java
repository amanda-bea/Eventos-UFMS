package com.ufms.eventos.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Data
public class Usuario {
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

    public Usuario(String nome) {
        this.nome = nome;
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
}