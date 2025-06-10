package com.ufms.eventos.ui;

import com.ufms.eventos.controller.PresencaConfirmadaController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PresencasConfirmadasFXMLController implements Initializable {

    @FXML private FlowPane eventoContainer;
    @FXML private Label tituloPrincipalLabel;

    private PresencaConfirmadaController presencaController;

    @FXML private FlowPane disponiveisContainer;
    @FXML private FlowPane indisponiveisContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.presencaController = new PresencaConfirmadaController();
        
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogado != null) {
            carregarPresencas(usuarioLogado);
        } else {
            if (disponiveisContainer != null) {
                disponiveisContainer.getChildren().add(new Label("Erro: Faça o login para ver suas presenças."));
            }
        }
    }

    private void carregarPresencas(Usuario usuarioLogado) {
        // Limpa ambos os containers
        disponiveisContainer.getChildren().clear();
        indisponiveisContainer.getChildren().clear();
        
        List<EventoMinDTO> meusEventos = presencaController.listarEventosComPresencaConfirmada(usuarioLogado);

        if (meusEventos.isEmpty()) {
            disponiveisContainer.getChildren().add(new Label("Você ainda não confirmou presença em nenhum evento."));
            return;
        }

        // Separa os eventos em duas listas
        List<EventoMinDTO> eventosAtivos = meusEventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        List<EventoMinDTO> eventosInativos = meusEventos.stream()
                .filter(e -> !"Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
        
        // Adiciona os cards de eventos ATIVOS no container de disponíveis
        if (eventosAtivos.isEmpty()) {
            disponiveisContainer.getChildren().add(new Label("Nenhuma presença confirmada em eventos ativos."));
        } else {
            eventosAtivos.forEach(evento -> disponiveisContainer.getChildren().add(criarCardEvento(evento)));
        }
        
        // Adiciona os cards de eventos INATIVOS no container de indisponíveis
        if (eventosInativos.isEmpty()) {
            indisponiveisContainer.getChildren().add(new Label("Nenhuma presença em eventos passados."));
        } else {
            eventosInativos.forEach(evento -> indisponiveisContainer.getChildren().add(criarCardEvento(evento)));
        }
    }
    /**
     * MÉTODO ATUALIZADO: A versão final, robusta e bonita para criar o card.
     */
    private VBox criarCardEvento(EventoMinDTO evento) {
        VBox cardPane = new VBox(5);
        cardPane.setPrefSize(210, 240);
        cardPane.setMinSize(210, 240);
        cardPane.setMaxSize(210, 240);
        cardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        cardPane.setCursor(Cursor.HAND);
        cardPane.setPadding(new Insets(10));
        cardPane.setAlignment(Pos.TOP_CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(130.0);
        imageView.setFitWidth(190.0);
        imageView.setPreserveRatio(true);

        try {
            String imagePath = evento.getImagem();
            if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
                imageView.setImage(new Image(new FileInputStream(imagePath)));
            } else {
                imageView.setImage(new Image(new FileInputStream("Sistema/imagem_eventos/placeholder.png")));
            }
        } catch (Exception e) {
            try {
                imageView.setImage(new Image(new FileInputStream("Sistema/imagem_eventos/placeholder.png")));
            } catch (Exception ex) {
                System.err.println("ERRO CRÍTICO: Imagem placeholder não encontrada!");
            }
        }
        
        // Etiqueta de status (opcional, mas útil para o usuário saber o estado)
        Label statusLabel = new Label(evento.getStatus());
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setPadding(new Insets(2, 6, 2, 6));
        String statusStyle = "-fx-background-radius: 5; -fx-background-color: ";
        switch (evento.getStatus().toLowerCase()) {
            case "ativo" -> statusStyle += "#5cb85c;";
            case "cancelado" -> statusStyle += "#d9534f;";
            default -> statusStyle += "#777777;"; // Inativo, Lotado, etc.
        }
        statusLabel.setStyle(statusStyle);

        StackPane imagemComStatusPane = new StackPane(imageView, statusLabel);
        StackPane.setAlignment(statusLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(statusLabel, new Insets(5, 5, 0, 0));

        VBox infoBox = new VBox(4);
        infoBox.setPadding(new Insets(8, 5, 0, 5));
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label nomeLabel = new Label(evento.getNome());
        nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label categoriaLabel = new Label(evento.getCategoria());
        categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        Label dataLabel = new Label(evento.getDataInicio());
        dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        
        infoBox.getChildren().addAll(nomeLabel, categoriaLabel, dataLabel);
        
        cardPane.getChildren().addAll(imagemComStatusPane, infoBox);
        cardPane.setOnMouseClicked(mouseEvent -> navegarParaDetalhes(mouseEvent, evento.getId()));

        return cardPane;
    }

    private Label criarLabelSeparador(String texto) {
        Label separador = new Label(texto);
        separador.setFont(Font.font("System", FontWeight.BOLD, 16));
        separador.setPadding(new Insets(10, 0, 5, 0));
        separador.setPrefWidth(740);
        separador.setTextFill(Color.web("#2f4a51"));
        return separador;
    }
    
    private void navegarParaDetalhes(MouseEvent event, Long eventoId) {
        if (eventoId == null) {
            mostrarAlerta(AlertType.ERROR, "Erro", "ID do evento é nulo. Não é possível navegar.");
            return;
        }

        try {
            // Pega a janela (Stage) a partir do componente que foi clicado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 1. SALVA O ESTADO ATUAL DA JANELA (se está maximizada ou não)
            boolean estavaMaximizado = stage.isMaximized();

            // 2. Carrega a nova tela
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/EventoDetalhado.fxml"));
            Parent root = loader.load();
            
            DetalhesEventoFXMLController controller = loader.getController();
            controller.carregarDadosDoEvento(eventoId);
            
            // 3. Substitui o conteúdo da cena atual
            stage.getScene().setRoot(root);
            stage.setTitle("Detalhes do Evento");

            // 4. RESTAURA O ESTADO DA JANELA
            stage.setMaximized(estavaMaximizado);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de detalhes.");
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}