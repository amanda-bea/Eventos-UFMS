package com.ufms.eventos.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)      // Opcional, mas bom para incluir campos da superclasse no toString
@NoArgsConstructor // Cria public Organizador() { super(); }
public class Organizador extends Usuario {
    // NÃO declare nome, email, senha, telefone aqui se já existem em Usuario.
    // Adicione apenas campos específicos do Organizador, se houver.
    // private String matriculaOrganizador; // Exemplo de campo específico

    public Organizador(String nome, String email, String senha, String telefone) {
        super(nome, email, senha, telefone); // Isso inicializa os campos herdados
        // Inicialize campos específicos do Organizador aqui, se houver
        // this.matriculaOrganizador = "ALGUM_VALOR";
    }

    public Organizador(String nomeParaUsuario) { // Renomeado para clareza
         super(); // Chama o construtor padrão de Usuario
         this.setNome(nomeParaUsuario); // Usa o setter herdado de Usuario (assumindo que existe e é public/protected)
    }


}