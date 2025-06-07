package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AdminController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EventosPendentesFXMLController implements Initializable {

    @FXML
    private TilePane eventoContainer;
    @FXML
    private Label tituloPrincipalLabel; // Dê este fx:id ao seu Label no FXML

    private AdminController adminController;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.adminController = new AdminController();

        // --- VERIFICAÇÃO DE PERMISSÃO ADICIONADA ---
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (usuarioLogado == null || !(usuarioLogado instanceof Admin)) {
            // Se não há usuário logado ou se ele NÃO é um Admin...
            tituloPrincipalLabel.setText("Acesso Negado");
            Label msgErro = new Label("Você não tem permissão para acessar esta página.");
            msgErro.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            eventoContainer.getChildren().clear();
            eventoContainer.getChildren().add(msgErro);
            // Opcional: desabilitar outros componentes da tela
        } else {
            // Se for um Admin, carrega os eventos normalmente.
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
            Label lblMensagem = new Label("Não há eventos para análise no momento.");
            lblMensagem.setFont(Font.font("System", 16));
            eventoContainer.getChildren().add(lblMensagem);
            return;
        }

        for (EventoMinDTO evento : eventosPendentes) {
            AnchorPane cardEvento = criarCardDeAnalise(evento);
            eventoContainer.getChildren().add(cardEvento);
        }
    }

    private AnchorPane criarCardDeAnalise(EventoMinDTO evento) {
        AnchorPane cardPane = new AnchorPane();
        cardPane.setPrefSize(177, 240); // Altura para caber os botões
        cardPane.setStyle("-fx-background-color: #fdf5e6; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        cardPane.setCursor(Cursor.HAND); // Muda o cursor para indicar que é clicável

        // 1. Imagem do Evento (carregada da URL)
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120.0); // Ajuste de altura para dar espaço aos outros textos
        imageView.setFitWidth(160.0);
        imageView.setLayoutX(8.0);
        imageView.setLayoutY(8.0);
        imageView.setPreserveRatio(false); // Estica a imagem para preencher o espaço

        try {
            // Tenta carregar a imagem da URL. O 'true' carrega em background.
            Image image = new Image(evento.getImagem(), true);
            imageView.setImage(image);
        } catch (Exception e) {
            // Se a URL for inválida ou houver erro, usa uma imagem local de placeholder.
            // Crie uma pasta 'img' nos seus resources para isso.
            System.err.println("Erro ao carregar imagem: " + evento.getImagem());
            imageView.setImage(new Image(getClass().getResourceAsStream("/img/placeholder.png")));
        }

        // 2. Nome do Evento
        Label nomeLabel = new Label(evento.getNome());
        nomeLabel.setLayoutX(10.0);
        nomeLabel.setLayoutY(135.0); // Posição abaixo da imagem
        nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        nomeLabel.setTextFill(Color.web("#2f4a51"));

        // 3. Categoria do Evento
        Label categoriaLabel = new Label(evento.getCategoria());
        categoriaLabel.setLayoutX(10.0);
        categoriaLabel.setLayoutY(155.0);
        categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        categoriaLabel.setTextFill(Color.web("#489ec1"));
        
        // 4. Data de Início do Evento
        Label dataLabel = new Label(evento.getDataInicio());
        dataLabel.setLayoutX(10.0);
        dataLabel.setLayoutY(175.0);
        dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        dataLabel.setTextFill(Color.web("#555555"));

        // Botões de Ação (Aprovar/Rejeitar)
        Button btnAprovar = new Button("Aprovar");
        btnAprovar.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAprovar.setOnAction(e -> handleAprovar(evento.getNome()));

        Button btnRejeitar = new Button("Rejeitar");
        btnRejeitar.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRejeitar.setOnAction(e -> handleRejeitar(evento.getNome()));
        
        HBox botoesBox = new HBox(10, btnAprovar, btnRejeitar);
        botoesBox.setPrefHeight(25.0);
        HBox.setHgrow(btnAprovar, Priority.ALWAYS);
        HBox.setHgrow(btnRejeitar, Priority.ALWAYS);
        btnAprovar.setMaxWidth(Double.MAX_VALUE);
        btnRejeitar.setMaxWidth(Double.MAX_VALUE);

        // Posiciona os botões na parte inferior do card
        AnchorPane.setBottomAnchor(botoesBox, 8.0);
        AnchorPane.setLeftAnchor(botoesBox, 8.0);
        AnchorPane.setRightAnchor(botoesBox, 8.0);

        // Adiciona todos os componentes ao painel do card
        cardPane.getChildren().addAll(imageView, nomeLabel, categoriaLabel, dataLabel, botoesBox);

        return cardPane;
    }
    
    // As funções handleAprovar, handleRejeitar e mostrarAlerta permanecem as mesmas
    // da resposta anterior, pois a lógica da UI não muda.
    private void handleAprovar(String nomeEvento) {
        boolean sucesso = adminController.aprovarEvento(nomeEvento);
        if (sucesso) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Evento aprovado e agora está Ativo.");
            carregarEventosParaAnalise();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível aprovar o evento.");
        }
    }

    private void handleRejeitar(String nomeEvento) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejeitar Evento");
        dialog.setHeaderText("Rejeitando o evento: " + nomeEvento);
        dialog.setContentText("Por favor, informe o motivo:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(motivo -> {
            if (motivo.trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Inválido", "O motivo não pode estar vazio.");
                return;
            }
            boolean sucesso = adminController.rejeitarEvento(nomeEvento, motivo);
            if (sucesso) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "O evento foi rejeitado.");
                carregarEventosParaAnalise();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível rejeitar o evento.");
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}