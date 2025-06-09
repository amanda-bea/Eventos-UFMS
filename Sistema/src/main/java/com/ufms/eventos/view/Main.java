package com.ufms.eventos.view;

import com.ufms.eventos.services.EventoService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Sistema iniciando... Verificando status de eventos e ações.");

        EventoService eventoService = new EventoService();
        eventoService.atualizarStatusDeEventosEAcoes();

        System.out.println("Verificação concluída. Carregando tela principal...");

        Parent root = FXMLLoader.load(getClass().getResource("/com/ufms/eventos/view/Login.fxml"));
        primaryStage.setTitle("Sistema de Eventos");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
