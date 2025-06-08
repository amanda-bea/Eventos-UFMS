package com.ufms.eventos.ui;

import com.ufms.eventos.controller.PresencaConfirmadaController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PresencasConfirmadasFXMLController implements Initializable {

    @FXML private TilePane eventoContainer;
    @FXML private Label tituloPrincipalLabel;

    private PresencaConfirmadaController presencaController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.presencaController = new PresencaConfirmadaController();
        
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogado == null) {
            eventoContainer.getChildren().add(new Label("Erro: Faça o login para ver suas presenças."));
            return;
        }
        
        carregarPresencas(usuarioLogado);
    }

    private void carregarPresencas(Usuario usuarioLogado) {
        eventoContainer.getChildren().clear();
        
        List<EventoMinDTO> meusEventos = presencaController.listarEventosComPresencaConfirmada(usuarioLogado);

        if (meusEventos.isEmpty()) {
            eventoContainer.getChildren().add(new Label("Você ainda não confirmou presença em nenhum evento."));
            return;
        }

        // Separa os eventos em duas listas
        List<EventoMinDTO> eventosAtivos = meusEventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        List<EventoMinDTO> eventosInativos = meusEventos.stream()
                .filter(e -> !"Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        // Adiciona os cards dos eventos ativos
        if (!eventosAtivos.isEmpty()) {
            eventoContainer.getChildren().add(criarLabelSeparador("Próximos Eventos"));
            for (EventoMinDTO evento : eventosAtivos) {
                eventoContainer.getChildren().add(criarCardEvento(evento));
            }
        }
        
        // Adiciona os cards dos eventos inativos
        if (!eventosInativos.isEmpty()) {
            eventoContainer.getChildren().add(criarLabelSeparador("Eventos Anteriores ou Cancelados"));
            for (EventoMinDTO evento : eventosInativos) {
                eventoContainer.getChildren().add(criarCardEvento(evento));
            }
        }
    }

    private AnchorPane criarCardEvento(EventoMinDTO evento) {
        AnchorPane cardPane = new AnchorPane();
        cardPane.setPrefSize(180, 210);
        cardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        cardPane.setCursor(Cursor.HAND);

        cardPane.setOnMouseEntered(e -> cardPane.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #489ec1; -fx-border-width: 1.5; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        cardPane.setOnMouseExited(e -> cardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        
        cardPane.setOnMouseClicked(e -> navegarParaDetalhes(e, evento.getId()));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(130.0);
        imageView.setFitWidth(160.0);
        imageView.setLayoutX(10.0);
        imageView.setLayoutY(10.0);
        try {
            imageView.setImage(new Image(evento.getImagem(), true));
        } catch (Exception ex) {
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/DetalhesEvento.fxml"));
            Parent root = loader.load();
            DetalhesEventoFXMLController controller = loader.getController();
            controller.carregarDadosDoEvento(eventoId);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Detalhes do Evento");
        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar alerta de erro
        }
    }
}