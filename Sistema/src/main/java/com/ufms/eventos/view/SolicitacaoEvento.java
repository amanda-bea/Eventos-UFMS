package com.ufms.eventos.view;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SolicitacaoEvento extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(600, 529);
        root.setStyle("-fx-background-color: #489ec1;");

        AnchorPane formPane = new AnchorPane();
        formPane.setLayoutX(242);
        formPane.setLayoutY(-8);
        formPane.setPrefSize(358, 536);
        formPane.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = new Label("Solicitação de Novo Evento");
        titleLabel.setLayoutX(83);
        titleLabel.setLayoutY(29);
        titleLabel.setTextFill(javafx.scene.paint.Color.web("#489ec1d1"));
        titleLabel.setFont(Font.font("System Bold", 15));

        Label nomeLabel = new Label("Nome do Evento");
        nomeLabel.setLayoutX(21);
        nomeLabel.setLayoutY(65);
        nomeLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        nomeLabel.setFont(Font.font("Nirmala UI Bold", 12));

        TextField nomeField = new TextField();
        nomeField.setLayoutX(21);
        nomeField.setLayoutY(94);
        nomeField.setPrefSize(289, 25);
        nomeField.setPromptText("Nome do Evento");
        nomeField.setFont(Font.font("Nirmala UI Semilight", 12));

        Label descricaoLabel = new Label("Descrição");
        descricaoLabel.setLayoutX(21);
        descricaoLabel.setLayoutY(134);
        descricaoLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        descricaoLabel.setFont(Font.font("Nirmala UI Bold", 12));

        TextArea descricaoArea = new TextArea();
        descricaoArea.setLayoutX(21);
        descricaoArea.setLayoutY(159);
        descricaoArea.setPrefSize(289, 37);
        descricaoArea.setPromptText("Descrição do Evento");
        descricaoArea.setFont(Font.font("Nirmala UI Semilight", 12));

        Label dataInicioLabel = new Label("Data de Início");
        dataInicioLabel.setLayoutX(21);
        dataInicioLabel.setLayoutY(204);
        dataInicioLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        dataInicioLabel.setFont(Font.font("Nirmala UI Bold", 12));

        DatePicker dataInicioPicker = new DatePicker();
        dataInicioPicker.setLayoutX(20);
        dataInicioPicker.setLayoutY(233);
        dataInicioPicker.setPrefSize(95, 25);
        dataInicioPicker.setPromptText("xx/xx/xxxx");

        Label dataFimLabel = new Label("Data de Fim");
        dataFimLabel.setLayoutX(215);
        dataFimLabel.setLayoutY(204);
        dataFimLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        dataFimLabel.setFont(Font.font("Nirmala UI Bold", 12));

        DatePicker dataFimPicker = new DatePicker();
        dataFimPicker.setLayoutX(215);
        dataFimPicker.setLayoutY(233);
        dataFimPicker.setPrefSize(95, 25);
        dataFimPicker.setPromptText("xx/xx/xxxx");

        Label departamentoLabel = new Label("Departamento");
        departamentoLabel.setLayoutX(20);
        departamentoLabel.setLayoutY(277);
        departamentoLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        departamentoLabel.setFont(Font.font("Nirmala UI Bold", 12));

        ComboBox<String> departamentoCombo = new ComboBox<>();
        departamentoCombo.setLayoutX(20);
        departamentoCombo.setLayoutY(297);
        departamentoCombo.getItems().addAll("Selecione", "FACOM", "INBIO", "INMA");

        Label categoriaLabel = new Label("Categoria");
        categoriaLabel.setLayoutX(215);
        categoriaLabel.setLayoutY(277);
        categoriaLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        categoriaLabel.setFont(Font.font("Nirmala UI Bold", 12));

        ComboBox<String> categoriaCombo = new ComboBox<>();
        categoriaCombo.setLayoutX(215);
        categoriaCombo.setLayoutY(297);
        categoriaCombo.getItems().addAll("Selecione", "Saúde", "Educação", "Esporte");

        Label imagemLabel = new Label("Inserir Imagem (URL)");
        imagemLabel.setLayoutX(24);
        imagemLabel.setLayoutY(336);
        imagemLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        imagemLabel.setFont(Font.font("Nirmala UI Bold", 12));

        TextField imagemField = new TextField();
        imagemField.setLayoutX(25);
        imagemField.setLayoutY(359);
        imagemField.setPrefSize(289, 25);
        imagemField.setPromptText("Link da foto");

        Label formLabel = new Label("Link de Formulário");
        formLabel.setLayoutX(23);
        formLabel.setLayoutY(403);
        formLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        formLabel.setFont(Font.font("Nirmala UI Bold", 12));

        TextField formField = new TextField();
        formField.setLayoutX(25);
        formField.setLayoutY(428);
        formField.setPrefSize(289, 25);
        formField.setPromptText("Nome do Evento");
        formField.setFont(Font.font("Nirmala UI Semilight", 12));

        Button adicionarButton = new Button("Adicionar Ação");
        adicionarButton.setLayoutX(123);
        adicionarButton.setLayoutY(478);
        adicionarButton.setStyle("-fx-background-color: #489ec1;");
        adicionarButton.setTextFill(javafx.scene.paint.Color.WHITE);

        // Adiciona todos os componentes ao formPane
        formPane.getChildren().addAll(titleLabel, nomeLabel, nomeField, descricaoLabel, descricaoArea,
                dataInicioLabel, dataInicioPicker, dataFimLabel, dataFimPicker,
                departamentoLabel, departamentoCombo, categoriaLabel, categoriaCombo,
                imagemLabel, imagemField, formLabel, formField, adicionarButton);

        // Adiciona formPane ao root
        root.getChildren().add(formPane);

        // Imagens
        ImageView img1 = new ImageView();
        img1.setFitWidth(176);
        img1.setFitHeight(165);
        img1.setLayoutX(31);
        img1.setLayoutY(179);
        img1.setPreserveRatio(true);
        img1.setImage(new Image("file:src/main/resources/img/logo.png"));

        ImageView img2 = new ImageView();
        img2.setFitWidth(200);
        img2.setFitHeight(37);
        img2.setLayoutX(0);
        img2.setLayoutY(6);
        img2.setPreserveRatio(true);
        img2.setImage(new Image("file:src/main/resources/img/seta.png"));

        root.getChildren().addAll(img1, img2);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Novo Evento");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
