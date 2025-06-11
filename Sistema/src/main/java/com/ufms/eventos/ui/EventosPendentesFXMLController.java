package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AdminController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EventosPendentesFXMLController implements Initializable {

    @FXML private TilePane eventoContainer;
    @FXML private Label tituloPrincipalLabel;

    private AdminController adminController;
    @FXML private HomebarFXMLController homebarController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.adminController = new AdminController();

        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogado == null || !(usuarioLogado instanceof Admin)) {
            tituloPrincipalLabel.setText("Acesso Negado");
            eventoContainer.getChildren().clear();
            eventoContainer.getChildren().add(new Label("Você não tem permissão para acessar esta página."));
        } else {
            carregarEventosParaAnalise();
        }

        if (homebarController != null) {
            // AVISA A BARRA QUE ESTA NÃO É A PÁGINA "HOME"
            homebarController.configurarParaPagina("EVENTOSPENDENTES"); 
        }
    }

    private void carregarEventosParaAnalise() {
        eventoContainer.getChildren().clear();
        List<EventoMinDTO> eventosPendentes = adminController.listarEventosAguardando();

        if (tituloPrincipalLabel != null) {
            tituloPrincipalLabel.setText("Eventos pendentes para análise (" + eventosPendentes.size() + ")");
        }

        if (eventosPendentes.isEmpty()) {
            eventoContainer.getChildren().add(new Label("Não há eventos para análise no momento."));
            return;
        }

        for (EventoMinDTO evento : eventosPendentes) {
            eventoContainer.getChildren().add(criarCardDeAnalise(evento));
        }
    }

    /**
     * Cria um card para exibição de um evento pendente para análise.
     * 
     * @param evento O evento a ser exibido no card
     * @return AnchorPane contendo o card formatado
     */
    private AnchorPane criarCardDeAnalise(EventoMinDTO evento) {
        // Criação do card principal
        AnchorPane card = new AnchorPane();
        card.setPrefSize(177, 177);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 5; -fx-border-radius: 5;");
        
        // Container para a imagem (usando StackPane para melhor centralização)
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(155, 90);
        imageContainer.setLayoutX(11);
        imageContainer.setLayoutY(10);
        imageContainer.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 3;");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(155);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(true);
        imageContainer.getChildren().add(imageView);
        
        try {
            if (evento.getImagem() != null && !evento.getImagem().isEmpty()) {
                File imageFile = new File(evento.getImagem());
                if (imageFile.exists() && imageFile.canRead()) {
                    FileInputStream fis = new FileInputStream(imageFile);
                    Image image = new Image(fis);
                    imageView.setImage(image);
                    fis.close();
                } else {
                    setPlaceholderImage(imageView);
                    System.err.println("Arquivo de imagem não encontrado: " + evento.getImagem());
                }
            } else {
                setPlaceholderImage(imageView);
            }
        } catch (Exception ex) {
            System.err.println("Erro ao carregar imagem para evento " + evento.getNome() + ": " + ex.getMessage());
            ex.printStackTrace();
            setPlaceholderImage(imageView);
        }
        
        // Labels para informações do evento
        Label nomeLabel = new Label(evento.getNome());
        nomeLabel.setLayoutX(11);
        nomeLabel.setLayoutY(105);
        nomeLabel.setPrefWidth(155);
        nomeLabel.setWrapText(true);
        nomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label dataLabel = new Label(evento.getDataInicio());
        dataLabel.setLayoutX(11);
        dataLabel.setLayoutY(130);
        dataLabel.setPrefWidth(155);
        dataLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        Label statusLabel = new Label("Aguardando Aprovação");
        statusLabel.setLayoutX(11);
        statusLabel.setLayoutY(150);
        statusLabel.setPrefWidth(155);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
        
        card.getChildren().addAll(imageContainer, nomeLabel, dataLabel, statusLabel);
        
        card.setOnMouseClicked(e -> {
            if (evento.getId() != null) {
                System.out.println("Clicou para ver detalhes do evento ID: " + evento.getId());
                navegarParaDetalhes(e, evento.getId());
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "ID do evento não encontrado.");
            }
        });
        
        return card;
    }

    /**
     * Carrega uma imagem placeholder quando a imagem do evento não pode ser carregada
     */
    private void setPlaceholderImage(ImageView imageView) {
        try {
            File placeholderFile = new File("imagem_eventos/placeholder.png");
            if (placeholderFile.exists()) {
                FileInputStream fis = new FileInputStream(placeholderFile);
                imageView.setImage(new Image(fis));
                fis.close();
            } else {
                System.err.println("Imagem placeholder não encontrada");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem placeholder: " + e.getMessage());
        }
    }

    private void navegarParaDetalhes(MouseEvent event, Long eventoId) {
        if (eventoId == null) {
            mostrarAlerta(AlertType.ERROR, "Erro de Dados", "Não foi possível obter o ID deste evento.");
            return;
        }

        try {
            // Pega a janela (Stage) a partir do componente que foi clicado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 1. SALVA O ESTADO DE TELA CHEIA ATUAL
            boolean estavaEmTelaCheia = stage.isFullScreen();

            // 2. Carrega a nova tela de detalhes
            URL fxmlUrl = getClass().getResource("/com/ufms/eventos/view/EventoDetalhado.fxml");
            if (fxmlUrl == null) {
                mostrarAlerta(AlertType.ERROR, "Erro de Arquivo", "Não foi possível encontrar o arquivo 'EventoDetalhado.fxml'.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // Passa o ID para o controller da tela de detalhes
            DetalhesEventoFXMLController detalhesController = loader.getController();
            detalhesController.carregarDadosDoEvento(eventoId);
            
            // 3. Substitui o conteúdo da cena atual
            stage.getScene().setRoot(root);
            stage.setTitle("Análise de Evento");

            // 4. RESTAURA O ESTADO DE TELA CHEIA
            stage.setFullScreen(estavaEmTelaCheia);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de detalhes.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}