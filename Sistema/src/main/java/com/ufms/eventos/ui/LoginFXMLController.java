package com.ufms.eventos.ui;

import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.model.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class LoginFXMLController {
    @FXML
    private TextField nomeField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private Label mensagemLabel;

    @FXML
    private Button loginButton;
 
    private Usuario usuarioLogado;

    @FXML
    public void initialize() {
        // Garante o estado inicial correto da mensagemLabel
        if (mensagemLabel != null) {
            mensagemLabel.setVisible(false);
            mensagemLabel.setManaged(false);
        }
    }

    @FXML
    private void autenticar() {
        String nome = nomeField.getText();
        String senha = senhaField.getText();

        // 1. Validação simples de campos vazios
        if (nome == null || nome.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            showMessageText("Por favor, preencha todos os campos.");
            return;
        }

        // Esconde mensagens anteriores e desabilita controles
        hideMessageLabel();
        setControlsDisabled(true);

        //autenticação fictícia
        if (nome.equals("admin") && senha.equals("admin")) {
            Admin admin = new Admin();
            admin.setNome(nome);
            admin.setSenha(senha);
            this.usuarioLogado = admin;
            
        }
        else{
            Organizador organizadorInstancia = new Organizador();
            organizadorInstancia.setNome(nome);    // 'nome' vindo do campo da tela de login
            organizadorInstancia.setSenha(senha);
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setSenha(senha);

        showMessageText("Login realizado com sucesso!");

        // Abrir proxima tela após 1 segundo
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/TelaSolicitacaoEvento.fxml"));
                
                Parent solicitacaoRoot = loader.load();

                // Obtem o controller da tela de solicitação
                SolicitarEventoFXMLController solicitarEventoCtrl = loader.getController();

                //  Passe a instância atual do LoginFXMLController ('this') para o controller da tela de solicitação.
                if (solicitarEventoCtrl != null) {
                    solicitarEventoCtrl.setLoginFXMLController(this); // 'this' refere-se à instância atual de LoginFXMLController
                } else {
                    // Isso seria um erro grave se o controller não for encontrado
                    System.err.println("CRÍTICO: O controller da tela TelaSolicitacaoEvento.fxml não foi encontrado ou não foi definido no FXML.");
                    showMessageText("Erro crítico ao preparar a próxima tela."); // Mostra na UI de login
                    setControlsDisabled(false); // Reabilita controles da tela de login
                    return; // Interrompe a transição
                }
                // Aqui mudar depois para abrir a tela cheia ou do jeito que estava antes
                Stage stage = (Stage) nomeField.getScene().getWindow();
                stage.setScene(new Scene(solicitacaoRoot));
                stage.setTitle("Solicitação de Evento");

            } catch (Exception e) {
                setControlsDisabled(false); // Reabilita controles na tela de login em caso de erro
                showMessageText("Erro ao carregar a próxima tela."); 
                e.printStackTrace();
            }
        });
        pause.play();
    }

    private void setControlsDisabled(boolean isDisabled) {
        if (loginButton != null) {
            loginButton.setDisable(isDisabled);
        }
        if (nomeField != null) {
            nomeField.setDisable(isDisabled);
        }
        if (senhaField != null) {
            senhaField.setDisable(isDisabled);
        }

        if (isDisabled) {
            hideMessageLabel(); // Esconde mensagens ao desabilitar (início do processo)
        }
    }

    private void showMessageText(String message) {
        if (mensagemLabel == null) return;
        mensagemLabel.setText(message);
        mensagemLabel.setVisible(true);
        mensagemLabel.setManaged(true);
    }

    private void hideMessageLabel() {
        if (mensagemLabel != null) {
            mensagemLabel.setVisible(false);
            mensagemLabel.setManaged(false);
        }
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }

    public void logout() {
        this.usuarioLogado = null;
        System.out.println("Usuário deslogado.");
        //aqui redicionamos para a tela de login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}