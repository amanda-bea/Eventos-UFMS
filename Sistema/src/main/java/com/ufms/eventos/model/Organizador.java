package com.ufms.eventos.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Organizador extends Usuario {


    public Organizador(String nome, String email, String senha, String telefone) {
        super(nome, email, senha, telefone); 
    }

    public Organizador(String nome, String senha) {
        super(nome, null, senha, null); // Telefone e email podem ser nulos
    }

}