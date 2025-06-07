package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeUsuarioFXMLController implements Initializable {

    // --- CAMPOS @FXML (DA BARRA DE TOPO E DO CONTEÚDO) ---
    @FXML private TilePane eventoContainer;
    @FXML private ScrollPane mainScrollPane;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;

    // --- CONTROLADORES E ESTADO ---
    private EventoController eventoController;
    private Popup filterTriggerPopup;

    // Campos para guardar o estado atual dos filtros
    private Categoria selectedCategoria = null;
    private Departamento selectedDepartamento = null;
    private String selectedModalidade = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        if (mainScrollPane != null) {
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
        createFilterTriggerPopup(); // Prepara o popup de gatilho do filtro
        executeSearchLogic();       // Executa uma busca inicial para carregar todos os eventos ativos
    }

    // --- MÉTODOS DE EXIBIÇÃO DE EVENTOS ---

    /**
     * Carrega e exibe os eventos na tela, aplicando os filtros e o termo de busca atuais.
     */
    private void carregarEventosFiltrados(String termoBusca, Categoria categoria, Departamento departamento, String modalidade) {
        if (eventoContainer == null) return;
        
        eventoContainer.getChildren().clear();

        // Chama o método do serviço que aceita os filtros
        List<EventoMinDTO> eventosFiltrados = eventoController.buscarEventosComFiltro(termoBusca, categoria, departamento, modalidade);

        if (eventosFiltrados.isEmpty()) {
            eventoContainer.getChildren().add(new Label("Nenhum evento encontrado com os filtros aplicados."));
            return;
        }

        for (EventoMinDTO evento : eventosFiltrados) {
            eventoContainer.getChildren().add(criarCardEventoAtivo(evento));
        }
    }

    /**
     * Cria o "card" visual para um único evento.
     */
    private AnchorPane criarCardEventoAtivo(EventoMinDTO evento) {
        AnchorPane cardPane = new AnchorPane();
        cardPane.setPrefSize(190, 210);
        cardPane.setStyle("-fx-background-color: #e9f5fa; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        cardPane.setCursor(Cursor.HAND);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(130.0);
        imageView.setFitWidth(170.0);
        imageView.setLayoutX(10.0);
        imageView.setLayoutY(10.0);
        try {
            imageView.setImage(new Image(evento.getImagem(), true));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/img/placeholder.png")));
        }

        Label nomeLabel = new Label(evento.getNome());
        nomeLabel.setLayoutX(10.0);
        nomeLabel.setLayoutY(145.0);
        nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label categoriaLabel = new Label(evento.getCategoria());
        categoriaLabel.setLayoutX(10.0);
        categoriaLabel.setLayoutY(165.0);
        categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        categoriaLabel.setTextFill(Color.web("#489ec1"));

        Label dataLabel = new Label(evento.getDataInicio());
        dataLabel.setLayoutX(10.0);
        dataLabel.setLayoutY(185.0);
        dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));

        cardPane.getChildren().addAll(imageView, nomeLabel, categoriaLabel, dataLabel);

        cardPane.setOnMouseClicked(mouseEvent -> {
            System.out.println("Card clicado! ID do Evento: " + evento.getId());
            // Lógica de navegação para detalhes do evento
        });

        return cardPane;
    }


    // --- MÉTODOS DE FILTRO E BUSCA ---

    private void createFilterTriggerPopup() {
        if (searchTextField == null) return;
        filterTriggerPopup = new Popup();
        filterTriggerPopup.setAutoHide(true);
        Hyperlink openFilterLink = new Hyperlink("Abrir Filtros Avançados");
        openFilterLink.setOnAction(this::handleOpenFilterPopup);
        VBox popupContent = new VBox(openFilterLink);
        popupContent.setPadding(new Insets(10));
        popupContent.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");
        filterTriggerPopup.getContent().add(popupContent);
    }

    @FXML
    private void handleSearchFieldClick(MouseEvent event) {
        if (filterTriggerPopup.isShowing()) {
            filterTriggerPopup.hide();
        } else {
            Node ownerNode = searchTextField;
            Window window = ownerNode.getScene().getWindow();
            Point2D screenCoords = ownerNode.localToScreen(0, ownerNode.getBoundsInLocal().getHeight());
            filterTriggerPopup.show(window, screenCoords.getX(), screenCoords.getY() + 5);
        }
    }
    
    @FXML
    private void handleOpenFilterPopup(ActionEvent event) {
        if (filterTriggerPopup.isShowing()) filterTriggerPopup.hide();

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(searchTextField.getScene().getWindow());
        popupStage.setTitle("Filtrar Eventos");

        VBox popupRoot = new VBox(20);
        popupRoot.setPadding(new Insets(25));
        popupRoot.setAlignment(Pos.CENTER_LEFT);

        ComboBox<Categoria> categoriaCombo = new ComboBox<>(FXCollections.observableArrayList(Categoria.values()));
        categoriaCombo.setPromptText("Todas as Categorias");
        if (selectedCategoria != null) categoriaCombo.setValue(selectedCategoria);

        ComboBox<Departamento> departamentoCombo = new ComboBox<>(FXCollections.observableArrayList(Departamento.values()));
        departamentoCombo.setPromptText("Todos os Departamentos");
        if (selectedDepartamento != null) departamentoCombo.setValue(selectedDepartamento);

        ComboBox<String> modalidadeCombo = new ComboBox<>(FXCollections.observableArrayList("Presencial", "Online"));
        modalidadeCombo.setPromptText("Todas as Modalidades");
        if (selectedModalidade != null) modalidadeCombo.setValue(selectedModalidade);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.add(new Label("Categoria:"), 0, 0); grid.add(categoriaCombo, 1, 0);
        grid.add(new Label("Departamento:"), 0, 1); grid.add(departamentoCombo, 1, 1);
        grid.add(new Label("Modalidade:"), 0, 2); grid.add(modalidadeCombo, 1, 2);

        Button aplicarButton = new Button("Aplicar Filtros");
        aplicarButton.setDefaultButton(true);
        aplicarButton.setOnAction(e -> {
            selectedCategoria = categoriaCombo.getValue();
            selectedDepartamento = departamentoCombo.getValue();
            selectedModalidade = modalidadeCombo.getValue();
            executeSearchLogic();
            popupStage.close();
        });

        Button limparButton = new Button("Limpar Filtros");
        limparButton.setOnAction(e -> {
            selectedCategoria = null;
            selectedDepartamento = null;
            selectedModalidade = null;
            executeSearchLogic();
            popupStage.close();
        });
        
        HBox buttonsBox = new HBox(10, aplicarButton, limparButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        popupRoot.getChildren().addAll(new Label("Opções de Filtro"), grid, new Separator(), buttonsBox);
        
        popupStage.setScene(new Scene(popupRoot));
        popupStage.showAndWait();
    }

    @FXML
    private void executeSearchAndHideTrigger(ActionEvent event) {
        if (filterTriggerPopup != null && filterTriggerPopup.isShowing()) {
            filterTriggerPopup.hide();
        }
        executeSearchLogic();
    }

    private void executeSearchLogic() {
        String searchTerm = searchTextField.getText();
        carregarEventosFiltrados(searchTerm, selectedCategoria, selectedDepartamento, selectedModalidade);
    }
}