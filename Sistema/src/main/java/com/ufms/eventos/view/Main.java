package com.ufms.eventos.view;
import java.io.File;

import com.ufms.eventos.services.EventoService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Sistema iniciando... Verificando status de eventos e ações.");

        EventoService eventoService = new EventoService();
        eventoService.atualizarStatusDeEventosEAcoes();

        // Criar pasta de imagens se não existir
        File diretorioImagens = new File("imagens_eventos");
        if (!diretorioImagens.exists()) {
            if (diretorioImagens.mkdirs()) {
                System.out.println("Diretório de imagens criado: " + diretorioImagens.getAbsolutePath());
            } else {
                System.err.println("Não foi possível criar o diretório de imagens");
            }
        }

        Parent root = FXMLLoader.load(getClass().getResource("/com/ufms/eventos/view/Login.fxml"));
        primaryStage.setTitle("SEUFMS");
        primaryStage.setScene(new Scene(root));
        
        // Configuração de tela cheia sem aviso
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}