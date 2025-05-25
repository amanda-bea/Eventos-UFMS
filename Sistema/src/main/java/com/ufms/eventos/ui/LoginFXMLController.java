package com.ufms.eventos.ui;

import com.ufms.eventos.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginFXMLController {
    private Usuario usuarioLogado;

    @FXML
    private TextField nomeField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private Label mensagemLabel;

    // Método chamado ao clicar no botão de login
    @FXML
    private void autenticar() {
        String nome = nomeField.getText();
        String senha = senhaField.getText();

        // Para teste: aceita qualquer nome e senha
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setSenha(senha);
        this.usuarioLogado = usuario;

        mensagemLabel.setText("Login realizado com sucesso!");
        // Aqui trocar de tela
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    public void logout() {
        this.usuarioLogado = null;
    }
}