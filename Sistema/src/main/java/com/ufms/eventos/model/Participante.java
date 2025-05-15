package com.ufms.eventos.model;

import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = true)
public class Participante extends Usuario {
    private String nome;
    private String telefone;
    private String email;
    private String senha;

    public Participante(String nome, String email, String senha, String telefone) {
        super(nome, email, senha, telefone);
    }

    public Participante() {
        // Construtor padrão
    }

    @Override
    public String toString() {
        return "Participante{" +
                "nome='" + nome + '\'' +
                "telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }

}
