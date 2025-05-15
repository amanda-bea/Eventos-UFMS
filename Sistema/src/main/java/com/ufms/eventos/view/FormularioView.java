package com.ufms.eventos.view;

import com.ufms.eventos.model.Formulario;
import com.ufms.eventos.services.FormularioService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FormularioView extends Application {

    private final FormularioService service = new FormularioService();

    @Override
    public void start(Stage stage) {
        ListView<String> listaFormularios = new ListView<>();
        Button btnCarregar = new Button("Carregar Formulários");

        btnCarregar.setOnAction(e -> {
            listaFormularios.getItems().clear();
            List<Formulario> formularios = service.listarFormularios();
            for (Formulario f : formularios) {
                String item = "Nome: " + f.getNome() + ", CPF: " + f.getCpf();
                listaFormularios.getItems().add(item);
            }
        });

        VBox layout = new VBox(10, btnCarregar, listaFormularios);
        Scene scene = new Scene(layout, 400, 300);

        stage.setTitle("Lista de Formulários");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
