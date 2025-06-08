package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class HomeFXMLController implements Initializable {

    // --- CAMPOS FXML DA TELA PRINCIPAL ---
    @FXML private TilePane eventoContainer;
    
    // --- REFERÊNCIA PARA O CONTROLLER DA BARRA INCLUÍDA ---
    // O nome 'homebarController' é gerado a partir do fx:id="homebar" no FXML
    @FXML private HomebarFXMLController homebarController; 

    // --- CONTROLADORES E ESTADO DOS FILTROS ---
    private EventoController eventoController;

    // O estado dos filtros agora é gerenciado pela tela principal
    private String termoAtual = "";
    private Categoria categoriaAtual = null;
    private Departamento departamentoAtual = null;
    private String modalidadeAtual = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        
        // A "PONTE" entre a barra e a tela principal é configurada aqui
        if (homebarController != null) {
            // Diz ao controller da barra qual função executar quando o usuário buscar/filtrar
            homebarController.setOnSearch(filterData -> {
                System.out.println("Filtro recebido da Homebar: " + filterData);
                
                // Atualiza o estado dos filtros nesta tela principal
                this.termoAtual = filterData.termoBusca();
                this.categoriaAtual = filterData.categoria();
                this.departamentoAtual = filterData.departamento();
                this.modalidadeAtual = filterData.modalidade();
                
                // Manda recarregar o conteúdo do TilePane com os novos filtros
                carregarEventosFiltrados();
            });
        }
        
        // Carga inicial (sem filtros) quando a tela abre
        carregarEventosFiltrados();
    }

    /**
     * Usa os campos de filtro da classe para buscar no serviço e exibir os eventos.
     */
    private void carregarEventosFiltrados() {
        if (eventoContainer == null) return;
        eventoContainer.getChildren().clear();

        List<EventoMinDTO> eventosFiltrados = eventoController.buscarEventosComFiltro(
            this.termoAtual, 
            this.categoriaAtual, 
            this.departamentoAtual, 
            this.modalidadeAtual
        );

        if (eventosFiltrados.isEmpty()) {
            eventoContainer.getChildren().add(new Label("Nenhum evento encontrado com os filtros atuais."));
        } else {
            for (EventoMinDTO evento : eventosFiltrados) {
                eventoContainer.getChildren().add(criarCardEventoAtivo(evento));
            }
        }
    }

    /**
     * Cria o "card" visual para um único evento.
     */
    private AnchorPane criarCardEventoAtivo(EventoMinDTO evento) {
        AnchorPane cardPane = new AnchorPane();
        cardPane.setPrefSize(190, 210);
        cardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        cardPane.setCursor(Cursor.HAND);

        cardPane.setOnMouseClicked(mouseEvent -> navegarParaDetalhes(mouseEvent, evento.getId()));

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
        return cardPane;
    }
    
    /**
     * Lógica para navegar para a tela de detalhes do evento.
     */
    private void navegarParaDetalhes(MouseEvent mouseEvent, Long eventoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/DetalhesEvento.fxml"));
            Parent root = loader.load();
            DetalhesEventoFXMLController detalhesController = loader.getController();
            detalhesController.carregarDadosDoEvento(eventoId);
            
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Detalhes do Evento");
        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar um alerta de erro para o usuário
        }
    }
}