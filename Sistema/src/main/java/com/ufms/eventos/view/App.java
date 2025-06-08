package com.ufms.eventos.view;

import com.ufms.eventos.services.EventoService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // --- LÓGICA DE INICIALIZAÇÃO DO SISTEMA ---
        System.out.println("Sistema iniciando... Verificando status de eventos e ações.");
        
        // Instancia o serviço principal
        EventoService eventoService = new EventoService(); 
        // Executa a rotina completa de atualização
        eventoService.atualizarStatusDeEventosEAcoes(); 
        
        System.out.println("Verificação concluída. Carregando tela principal...");
        // -----------------------------------------

        // Carrega a primeira tela do seu sistema (ex: Login.fxml)
        Parent root = FXMLLoader.load(getClass().getResource("/com/ufms/eventos/view/Login.fxml"));
        
        primaryStage.setTitle("Sistema de Eventos");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}