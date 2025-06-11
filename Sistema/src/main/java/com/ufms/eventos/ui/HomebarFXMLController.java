package com.ufms.eventos.ui;

import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.EventObject;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class HomebarFXMLController implements Initializable {

    // --- CAMPOS FXML DA BARRA ---
    @FXML private Label analiseLabel;
    @FXML private Label homeLabel;
    @FXML private Label presencasLabel;
    @FXML private Label meusEventosLabel;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private HBox buscaBox;
    @FXML private Button logoutButton;


    // --- LÓGICA DE FILTRO E COMUNICAÇÃO ---
    private Popup filterTriggerPopup;
    private Consumer<FilterData> onSearchCallback;
    public record FilterData(Categoria categoria, Departamento departamento, String modalidade, String termoBusca) {}
    public void setOnSearch(Consumer<FilterData> callback) {
        this.onSearchCallback = callback;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Prepara o popup de filtro
        createFilterTriggerPopup();

        // Pega o usuário da sessão para decidir o que mostrar
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();

        // Garante que os links opcionais comecem invisíveis
        if (analiseLabel != null) {
            analiseLabel.setVisible(false);
            analiseLabel.setManaged(false);
        }
        if (meusEventosLabel != null) {
            meusEventosLabel.setVisible(false);
            meusEventosLabel.setManaged(false);
        }
        
        if (usuarioLogado == null) return; // Sai se não houver usuário

        // Mostra o link de "Análise de Eventos" APENAS se for Admin
        if (SessaoUsuario.getInstancia().isAdmin()) {
            analiseLabel.setVisible(true);
            analiseLabel.setManaged(true);
        }

        // MOSTRA O BOTÃO "MEUS EVENTOS" PARA QUALQUER USUÁRIO LOGADO
        meusEventosLabel.setVisible(true);
        meusEventosLabel.setManaged(true);
    }

    /**
     * Cria o objeto Popup com o link "Abrir Filtros", mas ainda não o mostra.
     */
    private void createFilterTriggerPopup() {
        filterTriggerPopup = new Popup();
        filterTriggerPopup.setAutoHide(true);
        Hyperlink openFilterLink = new Hyperlink("Abrir Filtros Avançados");
        openFilterLink.setOnAction(this::handleOpenFilterPopup);
        VBox popupContent = new VBox(openFilterLink);
        popupContent.setPadding(new Insets(10));
        popupContent.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");
        filterTriggerPopup.getContent().add(popupContent);
    }

    /**
     * Chamado pelo onMouseClicked do TextField para mostrar o popup de gatilho.
     */
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
    
    /**
     * Cria e mostra a janela modal com os filtros avançados.
     */
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
        ComboBox<Departamento> departamentoCombo = new ComboBox<>(FXCollections.observableArrayList(Departamento.values()));
        departamentoCombo.setPromptText("Todos os Departamentos");
        ComboBox<String> modalidadeCombo = new ComboBox<>(FXCollections.observableArrayList("Presencial", "Online"));
        modalidadeCombo.setPromptText("Todas as Modalidades");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.add(new Label("Categoria:"), 0, 0); grid.add(categoriaCombo, 1, 0);
        grid.add(new Label("Departamento:"), 0, 1); grid.add(departamentoCombo, 1, 1);
        grid.add(new Label("Modalidade:"), 0, 2); grid.add(modalidadeCombo, 1, 2);
        
        Button aplicarButton = new Button("Aplicar Filtros");
        aplicarButton.setOnAction(e -> {
            executeSearchLogic(categoriaCombo.getValue(), departamentoCombo.getValue(), modalidadeCombo.getValue());
            popupStage.close();
        });

        Button limparButton = new Button("Limpar Filtros");
        limparButton.setOnAction(e -> {
            executeSearchLogic(null, null, null);
            popupStage.close();
        });
        
        HBox buttonsBox = new HBox(10, aplicarButton, limparButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        
        popupRoot.getChildren().addAll(new Label("Opções de Filtro"), grid, new Separator(), buttonsBox);

        popupStage.setScene(new Scene(popupRoot));
        popupStage.showAndWait();
    }
    
    @FXML
    private void handleSearch() {
        executeSearchLogic(null, null, null);
    }

    public void configurarParaPagina(String nomePagina) {
        boolean isHomePage = "HOME".equalsIgnoreCase(nomePagina);

        // Mostra a busca somente se estiver na página Home
        if (buscaBox != null) {
            buscaBox.setVisible(isHomePage);
            buscaBox.setManaged(isHomePage);
        }

        // Mostra o botão de logout em TODAS as outras páginas
        if (logoutButton != null) {
            logoutButton.setVisible(!isHomePage);
            logoutButton.setManaged(!isHomePage);
        }
    }

    /**
     * NOVO MÉTODO: Faz o logout do usuário e o redireciona para a tela de login.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        SessaoUsuario.getInstancia().logout(); // Limpa a sessão
        navegarPara(event, "/com/ufms/eventos/view/Login.fxml", "Login");
    }
    
    private void executeSearchLogic(Categoria categoria, Departamento departamento, String modalidade) {
        if (onSearchCallback != null) {
            String termo = searchTextField.getText();
            FilterData dadosDoFiltro = new FilterData(categoria, departamento, modalidade, termo);
            onSearchCallback.accept(dadosDoFiltro);
        }
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---
    @FXML private void handleNavigateAnalise(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/EventosPendentes.fxml", "Análise de Eventos"); }
    @FXML private void handleNavigateHome(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/Home.fxml", "Home"); }
    @FXML private void handleNavigatePresencas(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/PresencasConfirmadas.fxml", "Minhas Presenças"); }
    @FXML private void handleNavigateMeusEventos(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/MeusEventos.fxml", "Meus Eventos"); }


    private void navegarPara(EventObject event, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            boolean estavaMaximizado = stage.isMaximized();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            //ubstitui o conteúdo da tela atual pelo novo
            stage.getScene().setRoot(root);
            stage.setTitle(title);

            //RESTAURA O ESTADO DA JANELA
            stage.setMaximized(estavaMaximizado);

        } catch (IOException | NullPointerException e) {
            System.err.println("Falha ao carregar a tela: " + fxmlPath);
            e.printStackTrace();
        }
    }
}