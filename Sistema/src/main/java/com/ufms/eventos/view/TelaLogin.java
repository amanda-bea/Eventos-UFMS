package com.ufms.eventos.view;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TelaLogin extends Application {

    @Override
    public void start(Stage stage) {
        // Pane principal
        AnchorPane root = new AnchorPane();
        root.setPrefSize(600, 400);
        root.setStyle("-fx-background-color: #489ec1;");

        // Pane branco (lado direito)
        AnchorPane loginPane = new AnchorPane();
        loginPane.setPrefSize(302, 400);
        loginPane.setLayoutX(300);
        loginPane.setStyle("-fx-background-color: #ffffff;");

        // Label Login
        Label loginLabel = new Label("Login");
        loginLabel.setLayoutX(127);
        loginLabel.setLayoutY(62);
        loginLabel.setTextFill(javafx.scene.paint.Color.web("#489ec1d1"));
        loginLabel.setFont(Font.font("System Bold", 18));

        // Label Passaporte
        Label passaporteLabel = new Label("Passaporte");
        passaporteLabel.setLayoutX(14);
        passaporteLabel.setLayoutY(109);
        passaporteLabel.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        passaporteLabel.setFont(Font.font("Nirmala UI Bold", 12));

        // Campo de texto
        TextField passaporteField = new TextField();
        passaporteField.setLayoutX(14);
        passaporteField.setLayoutY(138);
        passaporteField.setPrefSize(259, 25);
        passaporteField.setPromptText("Insira seu passaporte");
        passaporteField.setFont(Font.font("Nirmala UI Semilight", 12));

        // Botão Entrar
        Button entrarBtn = new Button("Entrar");
        entrarBtn.setLayoutX(127);
        entrarBtn.setLayoutY(200);
        entrarBtn.setStyle("-fx-background-color: #489ec1;");
        entrarBtn.setTextFill(javafx.scene.paint.Color.WHITE);

        // Adiciona componentes ao loginPane
        loginPane.getChildren().addAll(loginLabel, passaporteLabel, passaporteField, entrarBtn);

        // Logo
        ImageView logoView = new ImageView();
        logoView.setFitHeight(165);
        logoView.setFitWidth(176);
        logoView.setLayoutX(63);
        logoView.setLayoutY(119);
        logoView.setPreserveRatio(true);
        logoView.setPickOnBounds(true);
        logoView.setImage(new Image("file:src/main/resources/img/logo.png")); // ajuste o caminho conforme necessário

        // Adiciona tudo ao root
        root.getChildren().addAll(loginPane, logoView);

        // Cena e stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Tela de Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
