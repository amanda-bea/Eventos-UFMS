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

        // --- LÓGICA DE AUTENTICAÇÃO CORRIGIDA ---
        // Aqui você faria uma busca no banco de dados. Para simulação:
        Usuario usuarioAutenticado;
        if (nome.equalsIgnoreCase("admin") && senha.equals("admin")) {
            // ÚNICA CONDIÇÃO PARA SER ADMIN
            usuarioAutenticado = new Admin(nome, senha);
        } else {
            // QUALQUER OUTRO LOGIN VÁLIDO É UM USUÁRIO COMUM
            // (Aqui, estamos assumindo que qualquer outra combinação é um usuário válido para teste)
            usuarioAutenticado = new Usuario(nome, senha);
        }
        // NÃO EXISTE MAIS a verificação para "organizador" aqui.
        // ------------------------------------

        if (usuarioAutenticado != null) {
            // Inicia a sessão global com o objeto correto (Admin ou Usuario)
            SessaoUsuario.getInstancia().login(usuarioAutenticado);
            
            showMessageText("Login realizado com sucesso! Redirecionando...");
            navegarParaTelaPrincipal();
        } else {
            // Esta parte seria alcançada se a busca no banco de dados falhasse
            showMessageText("Nome de usuário ou senha inválidos.");
        }
    }

    private void navegarParaTelaPrincipal() {
        setControlsDisabled(true);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(event -> {
            try {
                String fxmlPath;
                Usuario usuarioDaSessao = SessaoUsuario.getInstancia().getUsuarioLogado();

                // A navegação continua a mesma e funciona perfeitamente com a lógica corrigida.
                if (usuarioDaSessao instanceof Admin) {
                    fxmlPath = "/com/ufms/eventos/view/HomeAdmin.fxml";
                } else {
                    // Se não for Admin, é um Usuario (que pode ou não já ser um organizador).
                    // Ele vai para a home de usuário padrão.
                    fxmlPath = "/com/ufms/eventos/view/HomeUsuario.fxml";
                }

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