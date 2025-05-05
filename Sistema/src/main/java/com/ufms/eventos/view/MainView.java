package com.ufms.eventos.view;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.model.Evento;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView extends Application {

    private EventoController controller = new EventoController();

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do Evento");

        TextField dataField = new TextField();
        dataField.setPromptText("Data do Evento");

        Button adicionarButton = new Button("Adicionar Evento");
        adicionarButton.setOnAction(e -> {
            String nome = nomeField.getText();
            String data = dataField.getText();
            //controller.adicionarEvento(new Evento(nome, data));
            nomeField.clear();
            dataField.clear();
        });

        root.getChildren().addAll(nomeField, dataField, adicionarButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gerenciador de Eventos");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}