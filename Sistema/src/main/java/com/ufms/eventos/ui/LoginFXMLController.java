package com.ufms.eventos.ui;

import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFXMLController {

    @FXML private TextField nomeField;
    @FXML private PasswordField senhaField;
    @FXML private Label mensagemLabel;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        if (mensagemLabel != null) {
            mensagemLabel.setVisible(false);
        }
    }

    @FXML
    private void autenticar() {
        String nome = nomeField.getText();
        String senha = senhaField.getText();

        if (nome.trim().isEmpty() || senha.trim().isEmpty()) {
            showMessageText("Por favor, preencha todos os campos.");
            return;
        }

        // Simulação de autenticação
        Usuario usuarioAutenticado = null;
        
        // Verifica se é admin - APENAS este caso é especial
        if (nome.equalsIgnoreCase("admin") && senha.equals("admin")) {
            usuarioAutenticado = new Admin(nome, senha);
            System.out.println("Login como administrador");
        } else {
            // Qualquer outra combinação de login/senha cria um usuário comum
            usuarioAutenticado = new Usuario(nome, senha);
            System.out.println("Login como usuário comum: " + nome);
        }
        
        // Inicia a sessão com o usuário autenticado
        SessaoUsuario.getInstancia().login(usuarioAutenticado);
        System.out.println("DEBUG: Usuário salvo na sessão é do tipo: " + usuarioAutenticado.getClass().getName());
        
        showMessageText("Login realizado com sucesso! Redirecionando...");
        navegarParaTelaPrincipal();
    }

    private void navegarParaTelaPrincipal() {
        setControlsDisabled(true);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(event -> {
            try {
                String fxmlPath;
                fxmlPath = "/com/ufms/eventos/view/Home.fxml";
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.getScene().setRoot(root);
                stage.setTitle("Painel Principal");

            } catch (IOException e) {
                e.printStackTrace();
                showMessageText("Erro ao carregar a tela principal.");
                setControlsDisabled(false);
            }
        });
        pause.play();
    }
    
    private void showMessageText(String message) {
        if (mensagemLabel != null) {
            mensagemLabel.setText(message);
            mensagemLabel.setVisible(true);
        }
    }

    private void setControlsDisabled(boolean isDisabled) {
        if(loginButton != null) loginButton.setDisable(isDisabled);
        if(nomeField != null) nomeField.setDisable(isDisabled);
        if(senhaField != null) senhaField.setDisable(isDisabled);
    }
}