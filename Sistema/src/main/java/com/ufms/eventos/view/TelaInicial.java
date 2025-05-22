package com.ufms.eventos.view;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.dto.EventoMinDTO;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class TelaInicial extends Application {

    private EventoController eventoController = new EventoController();

    @Override
    public void start(Stage primaryStage) {
        ListView<EventoMinDTO> listView = new ListView<>();

        // Buscar eventos ativos resumidos
        List<EventoMinDTO> eventosMin = eventoController.listarEventosAtivosMin();
        listView.getItems().addAll(eventosMin);

        // Configurar como os itens são exibidos
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EventoMinDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " - " + item.getDataInicio());
                }
            }
        });

        // Evento de duplo clique
        /*
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                EventoMinDTO selecionado = listView.getSelectionModel().getSelectedItem();
                if (selecionado != null) {
                    EventoDTO eventoCompleto = eventoController.buscarEventoPorId(selecionado.getId());
                    abrirTelaDetalhes(eventoCompleto);
                }
            }
        });
         */

        VBox root = new VBox(listView);
        Scene scene = new Scene(root, 400, 600);

        primaryStage.setTitle("Eventos Ativos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Abre nova janela com os detalhes do evento
    private void abrirTelaDetalhes(EventoDTO evento) {
        Stage stage = new Stage();

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
            new javafx.scene.control.Label("Nome: " + evento.getNome()),
            new javafx.scene.control.Label("Data: " + evento.getDataInicio() + " até " + evento.getDataFim()),
            new javafx.scene.control.Label("Categoria: " + evento.getCategoria()),
            new javafx.scene.control.Label("Imagem: " + evento.getImagem()) // Pode trocar por ImageView depois
        );

        Scene scene = new Scene(layout, 300, 200);
        stage.setTitle("Detalhes do Evento");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
