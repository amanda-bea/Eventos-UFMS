package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AdminController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EventosPendentesFXMLController implements Initializable {

    @FXML private TilePane eventoContainer;
    @FXML private Label tituloPrincipalLabel;

    private AdminController adminController;

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

    private AnchorPane criarCardDeAnalise(EventoMinDTO evento) {
        AnchorPane cardPane = new AnchorPane();
        cardPane.setPrefSize(177, 208);
        cardPane.setStyle("-fx-background-color: #fdf5e6; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;");

        cardPane.setOnMouseEntered(e -> cardPane.setStyle("-fx-background-color: #f5efde; -fx-border-color: #489ec1; -fx-border-width: 1.5; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        cardPane.setOnMouseExited(e -> cardPane.setStyle("-fx-background-color: #fdf5e6; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        cardPane.setOnMouseClicked(e -> navegarParaDetalhes(evento.getId()));

        // Imagem do evento
        ImageView imageView = new ImageView();
        imageView.setFitWidth(155);
        imageView.setFitHeight(90);
        imageView.setLayoutX(11);
        imageView.setLayoutY(10);
        try {
            if (evento.getImagem() != null && !evento.getImagem().isEmpty()) {
                imageView.setImage(new Image(evento.getImagem(), true));
            }
        } catch (Exception ex) {
            // Se der erro, deixa sem imagem
        }

        // Nome do evento
        Label nomeLabel = new Label(evento.getNome());
        nomeLabel.setLayoutX(11);
        nomeLabel.setLayoutY(110);
        nomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        // Categoria
        Label categoriaLabel = new Label("Categoria: " + evento.getCategoria());
        categoriaLabel.setLayoutX(11);
        categoriaLabel.setLayoutY(135);
        categoriaLabel.setStyle("-fx-font-size: 12;");

        // Data de início
        Label dataLabel = new Label("Início: " + evento.getDataInicio());
        dataLabel.setLayoutX(11);
        dataLabel.setLayoutY(155);
        dataLabel.setStyle("-fx-font-size: 12;");

        cardPane.getChildren().addAll(imageView, nomeLabel, categoriaLabel, dataLabel);

        return cardPane;
    }

    private void navegarParaDetalhes(Long eventoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/EventoDetalhado.fxml"));
            Parent root = loader.load();

            DetalhesEventoFXMLController detalhesController = loader.getController();
            detalhesController.carregarDadosDoEvento(eventoId);

            Stage detalheStage = new Stage();
            detalheStage.setTitle("Análise de Evento");
            detalheStage.setScene(new Scene(root));
            detalheStage.initModality(Modality.APPLICATION_MODAL);
            detalheStage.initOwner(eventoContainer.getScene().getWindow());

            detalheStage.showAndWait();

            carregarEventosParaAnalise();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível abrir a tela de detalhes do evento.");
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