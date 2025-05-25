package com.ufms.eventos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)      // Opcional, mas bom para incluir campos da superclasse no toString
public class Admin extends Usuario {
    private String cargo; // só pra exemplo

    public Admin(String nome, String email, String senha, String telefone) {
        super(nome, email, senha, telefone);
        this.cargo = "Admin"; // Definindo cargo como Admin por padrão
    }

    public Admin() {
        super();
        this.cargo = "Admin"; // Definindo cargo como Admin por padrão
    }


}
