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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

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

    // --- LÓGICA DE COMUNICAÇÃO (CALLBACK) ---
    private Consumer<FilterData> onSearchCallback;

    /**
     * Um "pacote" de dados para enviar os filtros para a tela principal.
     */
    public record FilterData(Categoria categoria, Departamento departamento, String modalidade, String termoBusca) {}

    /**
     * Método público que permite à tela principal (ex: HomeUsuario) "assinar" o evento de busca.
     * @param callback A função que será executada quando o usuário aplicar os filtros.
     */
    public void setOnSearch(Consumer<FilterData> callback) {
        this.onSearchCallback = callback;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Lógica para mostrar/esconder links baseada no papel do usuário logado
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();

        boolean isAdmin = SessaoUsuario.getInstancia().isAdmin();
        // Qualquer usuário logado pode ver "Meus Eventos"
        boolean isLoggedIn = usuarioLogado != null;

        if (analiseLabel != null) {
            // Apenas admin vê "Eventos Pendentes"
            analiseLabel.setVisible(isAdmin);
            analiseLabel.setManaged(isAdmin);
        }
        
        if (meusEventosLabel != null) {
            // Qualquer usuário logado pode ver "Meus Eventos"
            meusEventosLabel.setVisible(isLoggedIn);
            meusEventosLabel.setManaged(isLoggedIn);
        }
    }

    // --- MÉTODOS DE AÇÃO DA UI (@FXML) ---

    @FXML
    private void handleSearch() {
        // O botão "Buscar" aplica os filtros atuais (com os ComboBoxes vazios, se não abertos)
        executeSearchLogic(null, null, null);
    }
    
    @FXML
    private void handleOpenFilterPopup() {
        // Cria e mostra a janela modal com os filtros avançados
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(searchTextField.getScene().getWindow());
        popupStage.setTitle("Filtrar Eventos");

        // --- Layout da Janela de Filtro ---
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
        
        // --- Botões da Janela de Filtro ---
        Button aplicarButton = new Button("Aplicar Filtros");
        aplicarButton.setDefaultButton(true);
        aplicarButton.setOnAction(e -> {
            executeSearchLogic(categoriaCombo.getValue(), departamentoCombo.getValue(), modalidadeCombo.getValue());
            popupStage.close();
        });

        Button limparButton = new Button("Limpar Filtros");
        limparButton.setOnAction(e -> {
            executeSearchLogic(null, null, null); // Chama a busca sem filtros
            popupStage.close();
        });

        HBox buttonsBox = new HBox(10, aplicarButton, limparButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        
        popupRoot.getChildren().addAll(new Label("Opções de Filtro"), grid, new Separator(), buttonsBox);

        popupStage.setScene(new Scene(popupRoot));
        popupStage.showAndWait();
    }
    
    /**
     * Coleta os dados dos filtros e do campo de busca, e notifica a tela principal.
     */
    private void executeSearchLogic(Categoria categoria, Departamento departamento, String modalidade) {
        if (onSearchCallback != null) {
            String termo = searchTextField.getText();
            FilterData dadosDoFiltro = new FilterData(categoria, departamento, modalidade, termo);
            
            // EXECUTA A FUNÇÃO DA TELA PRINCIPAL, passando os dados do filtro
            onSearchCallback.accept(dadosDoFiltro);
        }
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---
    @FXML private void handleNavigateAnalise(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/EventosPendentes.fxml", "Análise de Eventos"); }
    @FXML private void handleNavigateHome(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/HomeUsuario.fxml", "Home"); }
    @FXML private void handleNavigatePresencas(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/PresencasConfirmadas.fxml", "Minhas Presenças"); }
    @FXML private void handleNavigateMeusEventos(MouseEvent event) { navegarPara(event, "/com/ufms/eventos/view/MeusEventos.fxml", "Meus Eventos"); }

    private void navegarPara(EventObject event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle(title);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            // Mostrar um alerta de erro aqui
        }
    }
}