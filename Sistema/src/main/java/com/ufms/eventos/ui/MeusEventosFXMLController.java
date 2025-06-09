package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
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
import javafx.scene.control.Button;
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

public class MeusEventosFXMLController implements Initializable {

    // CORREÇÃO: As variáveis agora são do tipo FlowPane para corresponder ao FXML
    @FXML private FlowPane disponiveisContainer;
    @FXML private FlowPane indisponiveisContainer;
    
    @FXML private Button btnSolicitarNovoEvento;

    private EventoController eventoController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        
        if (usuarioLogado != null) {
            carregarMeusEventos(usuarioLogado);
        } else {
            disponiveisContainer.getChildren().add(new Label("Erro: Sessão de usuário inválida."));
        }
    }

    private void carregarMeusEventos(Usuario usuarioLogado) {
        disponiveisContainer.getChildren().clear();
        indisponiveisContainer.getChildren().clear();
        
        List<EventoMinDTO> todosMeusEventos = eventoController.buscarEventosPorUsuario(usuarioLogado);

        if (todosMeusEventos.isEmpty()) {
            disponiveisContainer.getChildren().add(new Label("Você ainda não criou nenhum evento."));
            return;
        }

        List<EventoMinDTO> eventosAtivos = todosMeusEventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        List<EventoMinDTO> eventosInativos = todosMeusEventos.stream()
                .filter(e -> !"Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
        
        if (eventosAtivos.isEmpty()) {
            disponiveisContainer.getChildren().add(new Label("Nenhum evento ativo no momento."));
        } else {
            eventosAtivos.forEach(evento -> disponiveisContainer.getChildren().add(criarCardMeuEvento(evento)));
        }
        
        if (eventosInativos.isEmpty()) {
            indisponiveisContainer.getChildren().add(new Label("Nenhum evento inativo ou pendente."));
        } else {
            eventosInativos.forEach(evento -> indisponiveisContainer.getChildren().add(criarCardMeuEvento(evento)));
        }
    }

    private VBox criarCardMeuEvento(EventoMinDTO evento) {
        VBox cardPane = new VBox(5);
        cardPane.setPrefSize(210, 240);
        cardPane.setMinSize(210, 240);
        cardPane.setMaxSize(210, 240);
        cardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        cardPane.setCursor(Cursor.HAND);
        cardPane.setPadding(new Insets(10));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(130.0);
        imageView.setFitWidth(190.0);
        imageView.setPreserveRatio(true);

        try {
            String imagePath = evento.getImagem(); // Usando getImagemPath
            if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
                imageView.setImage(new Image(new FileInputStream(imagePath)));
            } else {
                imageView.setImage(new Image(new FileInputStream("Sistema/imagem_eventos/placeholder.png")));
            }
        } catch (Exception e) {
            System.err.println("Falha ao carregar imagem para '" + evento.getNome() + "'. Usando placeholder.");
            try {
                imageView.setImage(new Image(new FileInputStream("Sistema/imagem_eventos/placeholder.png")));
            } catch (Exception ex) {
                System.err.println("Placeholder não encontrado!");
            }
        }

        Label statusLabel = new Label(evento.getStatus());
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setPadding(new Insets(2, 6, 2, 6));
        String statusStyle = "-fx-background-radius: 5; -fx-background-color: ";
        switch (evento.getStatus().toLowerCase()) {
            case "ativo" -> statusStyle += "#5cb85c;";
            case "aguardando aprovação" -> statusStyle += "#f0ad4e;";
            case "rejeitado", "cancelado" -> statusStyle += "#d9534f;";
            default -> statusStyle += "#777777;";
        }
        statusLabel.setStyle(statusStyle);

        StackPane imagemComStatusPane = new StackPane(imageView, statusLabel);
        StackPane.setAlignment(statusLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(statusLabel, new Insets(5, 5, 0, 0));

        VBox infoBox = new VBox(4);
        infoBox.setPadding(new Insets(8, 0, 0, 0));
        
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

    @FXML
    private void handleSolicitarNovoEvento() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/ufms/eventos/view/TelaSolicitacaoEvento.fxml"));
            Stage stage = (Stage) btnSolicitarNovoEvento.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navegarParaDetalhes(MouseEvent event, Long eventoId) {
        if (eventoId == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "ID do evento é nulo. Não é possível navegar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/EventoDetalhado.fxml"));
            Parent root = loader.load();
            DetalhesEventoFXMLController controller = loader.getController();
            controller.carregarDadosDoEvento(eventoId);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Detalhes do Evento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}