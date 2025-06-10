package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;

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
                eventoContainer.getChildren().add(criarCardEvento(evento));
            }
        }
    }

    /**
     * Cria o card visual padrão para um único evento.
     * Usa um layout VBox estável e não mostra a etiqueta de status,
     * ideal para a visualização de usuários comuns.
     *
     * @param evento O DTO com as informações do evento.
     * @return Um VBox que representa o card completo e clicável.
     */
    private VBox criarCardEvento(EventoMinDTO evento) {
        // 1. O card é um VBox para empilhar a imagem e os textos
        VBox cardPane = new VBox(5); // 5px de espaçamento vertical
        cardPane.setPrefSize(210, 240);
        cardPane.setMinSize(210, 240);
        cardPane.setMaxSize(210, 240);
        cardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        cardPane.setCursor(Cursor.HAND);
        cardPane.setPadding(new Insets(10));
        cardPane.setAlignment(Pos.TOP_CENTER); // Centraliza a imagem e o VBox de textos

        // 2. Imagem do Evento (sem o status)
        ImageView imageView = new ImageView();
        imageView.setFitHeight(130.0);
        imageView.setFitWidth(190.0);
        imageView.setPreserveRatio(true); // Garante que a imagem não seja distorcida

        try {
            String imagePath = evento.getImagem(); // Usando o campo de caminho do arquivo
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
                System.err.println("ERRO CRÍTICO: Imagem placeholder não encontrada!");
            }
        }

        // 3. Informações de Texto
        VBox infoBox = new VBox(4);
        infoBox.setPadding(new Insets(8, 5, 0, 5));
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label nomeLabel = new Label(evento.getNome());
        nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label categoriaLabel = new Label(evento.getCategoria());
        categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        categoriaLabel.setTextFill(Color.web("#489ec1"));
        
        Label dataLabel = new Label(evento.getDataInicio());
        dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        
        infoBox.getChildren().addAll(nomeLabel, categoriaLabel, dataLabel);
        
        // 4. Montagem Final do Card
        // Adiciona a imagem e o VBox de textos ao card principal.
        // A etiqueta de STATUS foi removida.
        cardPane.getChildren().addAll(imageView, infoBox);

        // Ação de clique para navegar para os detalhes
        cardPane.setOnMouseClicked(mouseEvent -> navegarParaDetalhes(mouseEvent, evento.getId()));

        return cardPane;
    }

    /**
     * Lógica para navegar para a tela de detalhes do evento.
     */
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
            // Mostrar um alerta de erro para o usuário
        }
    }
}