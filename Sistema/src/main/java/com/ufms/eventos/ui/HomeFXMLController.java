package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.ui.HomebarFXMLController.FilterData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
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

public class HomeFXMLController implements Initializable {

    // --- CAMPOS FXML DA TELA PRINCIPAL ---
    @FXML private TilePane eventoContainer;
    
    // --- REFERÊNCIA PARA O CONTROLLER DA BARRA INCLUÍDA ---
    @FXML private HomebarFXMLController homebarController; 

    // --- CONTROLADORES E ESTADO DOS FILTROS ---
    private EventoController eventoController;

    private String termoAtual = "";
    private Categoria categoriaAtual = null;
    private Departamento departamentoAtual = null;
    private String modalidadeAtual = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        
        if (homebarController != null) {
            // AVISA A BARRA QUE ESTA É A PÁGINA "HOME"
            homebarController.configurarParaPagina("HOME");
            homebarController.setOnSearch(this::aplicarFiltroEBuscar);
        }
        
        //sem filtros quando a tela abre
        carregarEventosFiltrados();
    }

    private void aplicarFiltroEBuscar(FilterData filterData) {
        System.out.println("TELA PRINCIPAL: Filtro recebido! " + filterData);
        
        this.termoAtual = filterData.termoBusca();
        this.categoriaAtual = filterData.categoria();
        this.departamentoAtual = filterData.departamento();
        this.modalidadeAtual = filterData.modalidade();
        
        carregarEventosFiltrados();
    }

    private void carregarEventosFiltrados() {
        if (eventoContainer == null) return;
        eventoContainer.getChildren().clear();

        List<EventoMinDTO> eventosFiltrados = eventoController.buscarEventosComFiltro(
            this.termoAtual, this.categoriaAtual, this.departamentoAtual, this.modalidadeAtual
        );

        if (eventosFiltrados.isEmpty()) {
            eventoContainer.getChildren().add(new Label("Nenhum evento encontrado com os filtros atuais."));
        } else {
            for (EventoMinDTO evento : eventosFiltrados) {
                eventoContainer.getChildren().add(criarCardEvento(evento));
            }
        }
    }

    private VBox criarCardEvento(EventoMinDTO evento) {
    // 1. O card é um VBox para empilhar a imagem e os textos
    VBox cardPane = new VBox(6); // Reduzir o espaçamento vertical entre elementos de 8 para 6
    cardPane.setPrefSize(260, 270); // Reduzir a altura de 300 para 270
    cardPane.setMinSize(260, 270);
    cardPane.setMaxSize(260, 270);
    
    cardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);");
    cardPane.setCursor(Cursor.HAND);
    cardPane.setPadding(new Insets(12, 15, 10, 15)); // Ajustar padding: menos no topo e base
    cardPane.setAlignment(Pos.TOP_CENTER);

    // 2. Container para imagem com altura fixa
    VBox imageContainer = new VBox();
    imageContainer.setMinHeight(160);
    imageContainer.setMaxHeight(160);
    imageContainer.setAlignment(Pos.CENTER);
    
    ImageView imageView = new ImageView();
    imageView.setFitHeight(160.0); 
    imageView.setFitWidth(230.0); 
    imageView.setPreserveRatio(true);
    
    imageContainer.getChildren().add(imageView);

    try {
        String imagePath = evento.getImagem();
        if (imagePath != null && !imagePath.isEmpty()) {
            // PRIORIDADE 1: Arquivo local em imagens_eventos/
            File imagemLocal = new File(imagePath);
            if (imagemLocal.exists()) {
                imageView.setImage(new Image(new FileInputStream(imagemLocal)));
            } 
            // PRIORIDADE 2: Se o caminho direto falhar, tente extrair nome do arquivo
            else {
                String nomeArquivo = new File(imagePath).getName();
                File imagemPastaImagens = new File("imagens_eventos", nomeArquivo);
                if (imagemPastaImagens.exists()) {
                    imageView.setImage(new Image(new FileInputStream(imagemPastaImagens)));
                } else {
                    // Placeholder
                    File placeholder = new File("imagens_eventos/placeholder.png");
                    if (placeholder.exists()) {
                        imageView.setImage(new Image(new FileInputStream(placeholder)));
                    }
                }
            }
        } 
    } catch (Exception e) {
        System.err.println("Falha ao carregar imagem para '" + evento.getNome() + "': " + e.getMessage());
        try {
            File placeholder = new File("imagens_eventos/placeholder.png");
            if (placeholder.exists()) {
                imageView.setImage(new Image(new FileInputStream(placeholder)));
            }
        } catch (Exception ex) {
            System.err.println("ERRO CRÍTICO: Imagem placeholder não encontrada!");
        }
    }

    // 3. Box de informações com altura dinâmica
    VBox infoBox = new VBox(4); // Reduzir espaçamento de 6 para 4
    infoBox.setPadding(new Insets(8, 4, 0, 4)); // Reduzir padding lateral e superior
    infoBox.setAlignment(Pos.TOP_LEFT); // Alinhar ao topo e à esquerda
    VBox.setVgrow(infoBox, javafx.scene.layout.Priority.ALWAYS); // Permitir crescimento

    Label nomeLabel = new Label(evento.getNome());
    nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 16)); 
    nomeLabel.setWrapText(true);
    nomeLabel.setMaxHeight(40); // Limitar altura para evitar crescimento excessivo

    Label categoriaLabel = new Label(evento.getCategoria());
    categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
    categoriaLabel.setTextFill(Color.web("#489ec1"));
    
    Label dataLabel = new Label(evento.getDataInicio());
    dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 13)); 
    
    infoBox.getChildren().addAll(nomeLabel, categoriaLabel, dataLabel);

    cardPane.getChildren().addAll(imageContainer, infoBox);
    cardPane.setOnMouseClicked(mouseEvent -> navegarParaDetalhes(mouseEvent, evento.getId()));

    return cardPane;
}


    private void navegarParaDetalhes(MouseEvent mouseEvent, Long eventoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/EventoDetalhado.fxml"));
            Parent root = loader.load();
            DetalhesEventoFXMLController detalhesController = loader.getController();
            detalhesController.carregarDadosDoEvento(eventoId);
            
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Detalhes do Evento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}