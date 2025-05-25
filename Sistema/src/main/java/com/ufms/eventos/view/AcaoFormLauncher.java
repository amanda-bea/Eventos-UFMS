package com.ufms.eventos.view;
import com.ufms.eventos.ui.SolicitarAcaoFXMLController;
import com.ufms.eventos.ui.LoginFXMLController;


import com.ufms.eventos.dto.EventoDTO;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AcaoFormLauncher {

    private LoginFXMLController loginFXMLController;
    private EventoDTO eventoDTO;

    public AcaoFormLauncher(LoginFXMLController loginFXMLController, EventoDTO eventoDTO) {
        this.loginFXMLController = loginFXMLController;
        this.eventoDTO = eventoDTO;
    }

    public void abrirFormulario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/acao.fxml"));
            Parent root = loader.load();

            // Pega o controller da tela carregada
            SolicitarAcaoFXMLController controller = loader.getController();

            // Passa o login e evento para o controller
            controller.setLoginFXMLController(loginFXMLController);
            controller.setEventoDTO(eventoDTO);

            // Cria uma nova janela e mostra
            Stage stage = new Stage();
            stage.setTitle("Solicitar Ação");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Aqui pode mostrar alerta ou log de erro
        }
    }
}
