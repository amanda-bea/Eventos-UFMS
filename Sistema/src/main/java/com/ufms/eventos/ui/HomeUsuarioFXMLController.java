package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeUsuarioFXMLController implements Initializable {

    @FXML
    private TilePane eventoContainer;

    @FXML
    private ScrollPane mainScrollPane;

    private EventoController eventoController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Melhora a aparência
        carregarEventosAtivos();
    }

    /**
     * Busca os eventos com status "Ativo" no serviço e os exibe na tela.
     */
    private void carregarEventosAtivos() {
        eventoContainer.getChildren().clear();
        List<EventoMinDTO> eventosAtivos = eventoController.listarEventosAtivosMin();

        if (eventosAtivos.isEmpty()) {
            Label lblMensagem = new Label("Não há eventos disponíveis no momento.");
            eventoContainer.getChildren().add(lblMensagem);
            return;
        }

        for (EventoMinDTO evento : eventosAtivos) {
            AnchorPane cardEvento = criarCardEventoAtivo(evento);
            eventoContainer.getChildren().add(cardEvento);
        }
    }

    /**
     * Cria um "card" (painel) visual para um único evento disponível.
     */
    private AnchorPane criarCardEventoAtivo(EventoMinDTO evento) {
        AnchorPane cardPane = new AnchorPane();
        cardPane.setPrefSize(190, 210); // Tamanho do card
        cardPane.setStyle("-fx-background-color: #e9f5fa; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        cardPane.setCursor(Cursor.HAND);

        // Imagem, Nome, Categoria e Data (igual ao que fizemos para o Admin)
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
        nomeLabel.setTextFill(Color.web("#2f4a51"));

        Label categoriaLabel = new Label(evento.getCategoria());
        categoriaLabel.setLayoutX(10.0);
        categoriaLabel.setLayoutY(165.0);
        categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        categoriaLabel.setTextFill(Color.web("#489ec1"));

        Label dataLabel = new Label(evento.getDataInicio());
        dataLabel.setLayoutX(10.0);
        dataLabel.setLayoutY(185.0);
        dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        dataLabel.setTextFill(Color.web("#555555"));

        cardPane.getChildren().addAll(imageView, nomeLabel, categoriaLabel, dataLabel);

        // AÇÃO DE CLIQUE: Captura o ID e chama a navegação
        cardPane.setOnMouseClicked(mouseEvent -> {
            System.out.println("Card clicado! ID do Evento: " + evento.getId());
            navegarParaDetalhes(evento.getId(), (Node) mouseEvent.getSource());
        });

        return cardPane;
    }

    /**
     * Lógica para navegar para a tela de detalhes do evento.
     */
    private void navegarParaDetalhes(Long eventoId, Node node) {
        // Exibe um alerta temporário para confirmar que o ID foi pego
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navegação");
        alert.setHeaderText("Indo para os detalhes do evento...");
        alert.setContentText("O ID do evento selecionado é: " + eventoId);
        alert.showAndWait();

        // TODO: Implementar a lógica real de navegação
        // Exemplo:
        // try {
        //    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/DetalhesEvento.fxml"));
        //    Parent root = loader.load();
        //
        //    DetalhesEventoController controller = loader.getController();
        //    controller.carregarDadosDoEvento(eventoId); // Passa o ID para o próximo controller
        //
        //    Stage stage = (Stage) node.getScene().getWindow();
        //    stage.getScene().setRoot(root);
        //
        // } catch (IOException e) {
        //    e.printStackTrace();
        // }
    }
}