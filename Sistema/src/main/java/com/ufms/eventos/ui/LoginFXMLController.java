package com.ufms.eventos.ui;

import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.model.Usuario;
// import javafx.application.Platform; // Não estritamente necessário para esta versão simplificada
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
    private Usuario usuarioLogado;

    @FXML
    private TextField nomeField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private Label mensagemLabel;

    @FXML
    private Button loginButton;

    // ProgressIndicator e suas constantes de estilo foram removidos

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

        Organizador organizadorInstancia = new Organizador(); // Ou usando um construtor mais completo
        organizadorInstancia.setNome(nome);    // 'nome' vindo do campo da tela de login
        organizadorInstancia.setSenha(senha);

        // 2. Lógica de Autenticação (seu código existente)
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setSenha(senha);
        this.usuarioLogado = organizadorInstancia;

        // 3. Mensagem de sucesso e transição (seu código existente)
        showMessageText("Login realizado com sucesso!");

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(event -> {
            try {
                // 1. Crie uma instância do FXMLLoader
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/TelaSolicitacaoEvento.fxml"));
                
                // 2. Carregue o Parent usando a instância do loader
                Parent solicitacaoRoot = loader.load();

                // 3. Obtenha a instância do controller DA TELA QUE VOCÊ ACABOU DE CARREGAR
                SolicitarEventoFXMLController solicitarEventoCtrl = loader.getController();

                // 4. !! PONTO CRÍTICO: Passe a instância atual do LoginFXMLController ('this')
                //    para o controller da tela de solicitação.
                if (solicitarEventoCtrl != null) {
                    solicitarEventoCtrl.setLoginFXMLController(this); // 'this' refere-se à instância atual de LoginFXMLController
                } else {
                    // Isso seria um erro grave se o controller não for encontrado
                    System.err.println("CRÍTICO: O controller da tela TelaSolicitacaoEvento.fxml não foi encontrado ou não foi definido no FXML.");
                    showMessageText("Erro crítico ao preparar a próxima tela."); // Mostra na UI de login
                    setControlsDisabled(false); // Reabilita controles da tela de login
                    return; // Interrompe a transição
                }

                // 5. Prossiga com a configuração da cena e do stage
                Stage stage = (Stage) nomeField.getScene().getWindow(); // Ou qualquer outro nó da cena atual
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
        // A cor do texto é definida estaticamente no FXML agora
        // mensagemLabel.setStyle(...); // NÃO HÁ MAIS MUDANÇA DE ESTILO AQUI
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
        // Adicionar lógica para retornar à tela de login, se necessário
    }
}