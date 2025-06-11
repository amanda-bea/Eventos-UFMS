package com.ufms.eventos.ui;

import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.services.UsuarioService;
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
    private UsuarioService usuarioService;

    @FXML
    public void initialize() {
        if (mensagemLabel != null) {
            mensagemLabel.setVisible(false);
        }
        usuarioService = new UsuarioService();
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
        
        // Verifica se é admin 
        if (nome.equalsIgnoreCase("admin") && senha.equals("admin")) {
            usuarioAutenticado = new Admin(nome, "admin@sistema.com", senha, "");
            System.out.println("Login como administrador");
            
            // Verifica se o admin já existe no banco
            if (usuarioService.buscarPorNome(nome) == null) {
                // Salva o admin no banco se ainda não existe
                boolean sucesso = usuarioService.cadastrarUsuario(usuarioAutenticado);
                if (sucesso) {
                    System.out.println("Administrador salvo no banco de dados!");
                } else {
                    System.out.println("AVISO: Não foi possível salvar o administrador no banco de dados.");
                }
            }
        } else {
            // Verifica se o usuário já existe no banco
            usuarioAutenticado = usuarioService.buscarPorNome(nome);
            
            if (usuarioAutenticado == null) {
                // Se não existe, cria um novo usuário e salva no banco
                usuarioAutenticado = new Usuario();
                usuarioAutenticado.setNome(nome);
                usuarioAutenticado.setSenha(senha);
                usuarioAutenticado.setEmail(nome + "@usuario.com");
                
                boolean sucesso = usuarioService.cadastrarUsuario(usuarioAutenticado);
                if (sucesso) {
                    System.out.println("Novo usuário salvo no banco de dados: " + nome);
                } else {
                    System.out.println("AVISO: Não foi possível salvar o usuário no banco de dados.");
                    usuarioAutenticado = new Usuario(nome, senha);
                }
            } else if (!usuarioAutenticado.getSenha().equals(senha)) {
                usuarioAutenticado.setSenha(senha);
                usuarioService.atualizarUsuario(usuarioAutenticado);
                System.out.println("Senha atualizada para o usuário: " + nome);
            }
            
            System.out.println("Login como usuário comum: " + nome);
        }
        
        // Inicia a sessão com o usuário autenticado
        SessaoUsuario.getInstancia().login(usuarioAutenticado);

        
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