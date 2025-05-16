package com.ufms.eventos.model;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // ver
public class Organizador extends Usuario {
    private String nome;
    private String telefone;
    private String email;
    private String senha;

    public Organizador(String nome, String email, String senha, String telefone) {
        super(nome, email, senha, telefone);
    }

    public Organizador() {
        // Construtor padrão
    }

    public Organizador(String nome) { //arrumar isso aqui
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Organizador{" +
                "nome='" + nome + '\'' +
                "telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }

}
