package com.ufms.eventos.ui;

import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HomeFXMLController {

    @FXML
    private TextField searchTextField;

    private Popup filterTriggerPopup;

    private Categoria selectedCategoria = null;
    private Departamento selectedDepartamento = null;
    private String selectedModalidade = null;

    @FXML
    public void initialize() {
        createFilterTriggerPopup();
        System.out.println("HomeFXMLController inicializado.");
    }

    private void createFilterTriggerPopup() {
        filterTriggerPopup = new Popup();
        filterTriggerPopup.setAutoHide(true);

        Hyperlink openFilterLink = new Hyperlink("Abrir Filtros");
        openFilterLink.setFont(Font.font("System", FontWeight.BOLD, 12));
        // Estilo para remover o anel de foco e manter a aparência do link
        openFilterLink.setStyle(
            "-fx-text-fill: #007bff;" +              // Cor do texto do link
            "-fx-focus-color: transparent;" +        // Remove a cor principal do anel de foco
            "-fx-faint-focus-color: transparent;" +  // Remove a cor secundária/suave do anel de foco
            "-fx-border-width: 0;" +                 // Garante que nenhuma borda seja desenhada no foco
            "-fx-border-insets: -1;"                // Ajuste para garantir que a borda não apareça
        );
        openFilterLink.setOnAction(this::handleOpenFilterPopup);

        VBox popupContent = new VBox(openFilterLink);
        popupContent.setPadding(new Insets(10));
        popupContent.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #ced4da;" +
            "-fx-border-width: 1px;" +
            "-fx-background-radius: 4px;" +
            "-fx-border-radius: 4px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0.3, 0, 1);"
        );
        filterTriggerPopup.getContent().add(popupContent);
    }


    @FXML
    private void handleSearchFieldClick(MouseEvent event) {
        if (filterTriggerPopup.isShowing()) {
            filterTriggerPopup.hide();
        } else {
            Node ownerNode = searchTextField;
            Window window = ownerNode.getScene().getWindow();
            Point2D textFieldScreenCoords = ownerNode.localToScreen(0, ownerNode.getBoundsInLocal().getHeight());
            double popupX = textFieldScreenCoords.getX();
            double popupY = textFieldScreenCoords.getY() + 5;
            filterTriggerPopup.show(window, popupX, popupY);
            System.out.println("Popup de gatilho de filtro exibido.");
        }
    }

    @FXML
    private void handleOpenFilterPopup(ActionEvent event) {
        if (filterTriggerPopup.isShowing()) {
            filterTriggerPopup.hide();
        }
        System.out.println("Abrindo popup de filtros principal...");

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        Window ownerWindow = searchTextField.getScene().getWindow();
        if (ownerWindow != null) {
            popupStage.initOwner(ownerWindow);
        }
        popupStage.setTitle("Filtrar Eventos");

        VBox popupRoot = new VBox(20);
        popupRoot.setPadding(new Insets(25));
        popupRoot.setAlignment(Pos.CENTER_LEFT);
        popupRoot.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Label titleLabel = new Label("Opções de Filtro");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#343a40"));
        VBox.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        String comboBoxStyle = "-fx-font-size: 13px; -fx-background-color: white; -fx-border-color: #ced4da; -fx-border-radius: 4px; -fx-background-radius: 4px;";
        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #495057;";

        ComboBox<Categoria> categoriaCombo = new ComboBox<>();
        categoriaCombo.setItems(FXCollections.observableArrayList(Categoria.values()));
        categoriaCombo.setPromptText("Todas as Categorias");
        if (selectedCategoria != null) categoriaCombo.setValue(selectedCategoria);
        categoriaCombo.setPrefWidth(300);
        categoriaCombo.setStyle(comboBoxStyle);
        Label categoriaLabel = new Label("Categoria:");
        categoriaLabel.setStyle(labelStyle);

        ComboBox<Departamento> departamentoCombo = new ComboBox<>();
        departamentoCombo.setItems(FXCollections.observableArrayList(Departamento.values()));
        departamentoCombo.setPromptText("Todos os Departamentos");
        if (selectedDepartamento != null) departamentoCombo.setValue(selectedDepartamento);
        departamentoCombo.setPrefWidth(300);
        departamentoCombo.setStyle(comboBoxStyle);
        Label departamentoLabel = new Label("Departamento:");
        departamentoLabel.setStyle(labelStyle);

        ComboBox<String> modalidadeCombo = new ComboBox<>();
        modalidadeCombo.setItems(FXCollections.observableArrayList("Presencial", "Online"));
        modalidadeCombo.setPromptText("Todas as Modalidades");
        if (selectedModalidade != null) modalidadeCombo.setValue(selectedModalidade);
        modalidadeCombo.setPrefWidth(300);
        modalidadeCombo.setStyle(comboBoxStyle);
        Label modalidadeLabel = new Label("Modalidade:");
        modalidadeLabel.setStyle(labelStyle);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.add(categoriaLabel, 0, 0); grid.add(categoriaCombo, 1, 0);
        grid.add(departamentoLabel, 0, 1); grid.add(departamentoCombo, 1, 1);
        grid.add(modalidadeLabel, 0, 2); grid.add(modalidadeCombo, 1, 2);

        String baseButtonStyle = "-fx-font-size: 13px; -fx-padding: 8px 15px; -fx-background-radius: 4px; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;";
        Button aplicarButton = new Button("Aplicar Filtros");
        aplicarButton.setStyle(baseButtonStyle + "-fx-background-color: #007bff;");
        aplicarButton.setDefaultButton(true);
        aplicarButton.setOnAction(e -> {
            selectedCategoria = categoriaCombo.getValue();
            selectedDepartamento = departamentoCombo.getValue();
            selectedModalidade = modalidadeCombo.getValue();
            System.out.println("Filtros aplicados: Categoria=" + selectedCategoria +
                               ", Departamento=" + selectedDepartamento +
                               ", Modalidade=" + selectedModalidade);
            executeSearchLogic();
            popupStage.close();
        });

        Button limparButton = new Button("Limpar Filtros");
        limparButton.setStyle(baseButtonStyle + "-fx-background-color: #6c757d;");
        limparButton.setOnAction(e -> {
            categoriaCombo.setValue(null);
            departamentoCombo.setValue(null);
            modalidadeCombo.setValue(null);
            selectedCategoria = null;
            selectedDepartamento = null;
            selectedModalidade = null;
            System.out.println("Filtros limpos.");
            executeSearchLogic();
        });

        Button cancelarButton = new Button("Cancelar");
        cancelarButton.setStyle(baseButtonStyle + "-fx-background-color: #dc3545;");
        cancelarButton.setCancelButton(true);
        cancelarButton.setOnAction(e -> popupStage.close());

        HBox buttonsBox = new HBox(10, aplicarButton, limparButton, cancelarButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(buttonsBox, new Insets(20, 0, 0, 0));

        popupRoot.getChildren().addAll(titleLabel, grid, new Separator(), buttonsBox);

        Scene popupScene = new Scene(popupRoot);
        popupStage.setScene(popupScene);
        popupStage.setResizable(false);
        popupStage.showAndWait();
    }

    @FXML
    private void executeSearchAndHideTrigger(ActionEvent event) {
        if (filterTriggerPopup.isShowing()) {
            filterTriggerPopup.hide();
        }
        executeSearchLogic();
    }

    private void executeSearchLogic() {
        String searchTerm = searchTextField.getText();
        System.out.println("Executando busca por: '" + searchTerm + "'");
        if (selectedCategoria != null) {
            System.out.println("Filtrando por Categoria: " + selectedCategoria.name());
        }
        if (selectedDepartamento != null) {
            System.out.println("Filtrando por Departamento: " + selectedDepartamento.name());
        }
        if (selectedModalidade != null && !selectedModalidade.isEmpty()) {
            System.out.println("Filtrando por Modalidade: " + selectedModalidade);
        }
        // Implemente a lógica de busca e atualização do TilePane aqui
    }
}