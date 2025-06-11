package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
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
import java.util.stream.Collectors;

public class MeusEventosFXMLController implements Initializable {

    // CORREÇÃO: As variáveis agora são do tipo FlowPane para corresponder ao FXML
    @FXML private FlowPane disponiveisContainer;
    @FXML private FlowPane indisponiveisContainer;
    
    @FXML private Button btnSolicitarNovoEvento;

    private EventoController eventoController;

    @FXML private HomebarFXMLController homebarController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        
        if (usuarioLogado != null) {
            carregarMeusEventos(usuarioLogado);
        } else {
            disponiveisContainer.getChildren().add(new Label("Erro: Sessão de usuário inválida."));
        }

        if (homebarController != null) {
            homebarController.configurarParaPagina("MEUSEVENTOS"); 
        }
    }

    private void carregarMeusEventos(Usuario usuarioLogado) {
        disponiveisContainer.getChildren().clear();
        indisponiveisContainer.getChildren().clear();
        
        List<EventoMinDTO> todosMeusEventos = eventoController.buscarEventosPorUsuario(usuarioLogado);

        if (todosMeusEventos.isEmpty()) {
            disponiveisContainer.getChildren().add(new Label("Você ainda não criou nenhum evento."));
            return;
        }

        List<EventoMinDTO> eventosAtivos = todosMeusEventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        List<EventoMinDTO> eventosInativos = todosMeusEventos.stream()
                .filter(e -> !"Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
        
        if (eventosAtivos.isEmpty()) {
            disponiveisContainer.getChildren().add(new Label("Nenhum evento ativo no momento."));
        } else {
            eventosAtivos.forEach(evento -> disponiveisContainer.getChildren().add(criarCardEvento(evento)));
        }
        
        if (eventosInativos.isEmpty()) {
            indisponiveisContainer.getChildren().add(new Label("Nenhum evento inativo ou pendente."));
        } else {
            eventosInativos.forEach(evento -> indisponiveisContainer.getChildren().add(criarCardEvento(evento)));
        }
    }

/**
 * Cria o card visual padrão para um único evento, combinando um bom design
 * com um layout VBox estável e responsivo.
 * @param evento O DTO com as informações do evento.
 * @return Um VBox que representa o card completo e clicável.
 */
private VBox criarCardEvento(EventoMinDTO evento) {
    // 1. O card principal é um VBox para estabilidade.
    VBox cardPane = new VBox(5); // 5px de espaçamento vertical
    cardPane.setPrefSize(210, 240);
    cardPane.setMinSize(210, 240);
    cardPane.setMaxSize(210, 240);
    cardPane.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
    cardPane.setCursor(Cursor.HAND);
    cardPane.setAlignment(Pos.TOP_CENTER);
    cardPane.setPadding(new Insets(10, 10, 8, 10)); // Reduzir padding inferior

    // 2. A imagem com a etiqueta de status sobreposta
    StackPane imagemComStatusPane = criarImagemComStatus(evento);
    
    // Ajustar tamanho específico do container de imagem para evitar redimensionamento
    imagemComStatusPane.setMinHeight(130);
    imagemComStatusPane.setPrefHeight(130);
    imagemComStatusPane.setMaxHeight(130);

    // 3. O container para as informações de texto
    VBox infoBox = new VBox(3); // Reduzir espaçamento para 3px
    infoBox.setPadding(new Insets(6, 4, 0, 4)); // Reduzir padding
    infoBox.setMinHeight(80); // Altura mínima para o texto
    infoBox.setAlignment(Pos.TOP_LEFT); // Alinhar ao topo!

    Label nomeLabel = new Label(evento.getNome());
    nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
    nomeLabel.setTextFill(Color.web("#2f4a51"));
    nomeLabel.setWrapText(true);
    nomeLabel.setMaxHeight(40); // Limitar altura para nomes muito longos

    Label categoriaLabel = new Label(evento.getCategoria());
    categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
    categoriaLabel.setTextFill(Color.web("#489ec1"));

    Label dataLabel = new Label("Início: " + evento.getDataInicio());
    dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
    dataLabel.setTextFill(Color.web("#666"));

    infoBox.getChildren().addAll(nomeLabel, categoriaLabel, dataLabel);
    
    // 4. Montagem final e ação de clique
    cardPane.getChildren().addAll(imagemComStatusPane, infoBox);
    cardPane.setOnMouseClicked(mouseEvent -> navegarParaDetalhes(mouseEvent, evento.getId()));

    return cardPane;
}
/**
 * Método auxiliar para criar o grupo de Imagem + Status.
 * @return Um StackPane contendo a imagem e a etiqueta de status.
 */
private StackPane criarImagemComStatus(EventoMinDTO evento) {
    ImageView imageView = new ImageView();
    imageView.setFitHeight(130.0);
    imageView.setFitWidth(190.0);
    imageView.setPreserveRatio(true);

    try {
        String imagePath = evento.getImagem();
        Image imagem = null;
        
        // PRIORIDADE 1: Verificar na pasta externa imagens_eventos
        if (imagePath != null && !imagePath.isEmpty()) {
            // Extrair nome do arquivo do caminho completo
            String nomeArquivo = new File(imagePath).getName();
            
            File imagemDiretorioLocal = new File("imagens_eventos", nomeArquivo);
            if (imagemDiretorioLocal.exists()) {
                imagem = new Image(new FileInputStream(imagemDiretorioLocal));
                System.out.println("DEBUG: Imagem carregada de imagens_eventos: " + imagemDiretorioLocal.getAbsolutePath());
            } 
            // PRIORIDADE 2: Tentar o caminho direto
            else if (new File(imagePath).exists()) {
                imagem = new Image(new FileInputStream(imagePath));
                System.out.println("DEBUG: Imagem carregada do caminho direto: " + imagePath);
            }
            // PRIORIDADE 3: Verificar nos recursos internos
            else {
                String resourcePath = "/img/" + nomeArquivo;
                java.io.InputStream resourceStream = getClass().getResourceAsStream(resourcePath);
                if (resourceStream != null) {
                    imagem = new Image(resourceStream);
                    System.out.println("DEBUG: Imagem carregada de recursos: " + resourcePath);
                }
            }
        }
        
        // Se não conseguiu carregar, usar placeholder
        if (imagem == null) {
            File placeholderFile = new File("imagens_eventos/placeholder.png");
            if (placeholderFile.exists()) {
                imagem = new Image(new FileInputStream(placeholderFile));
            } else {
                java.io.InputStream placeholderStream = getClass().getResourceAsStream("/img/placeholder.png");
                if (placeholderStream != null) {
                    imagem = new Image(placeholderStream);
                }
            }
        }
        
        imageView.setImage(imagem);
        
    } catch (Exception e) {
        System.err.println("Erro ao carregar imagem para evento " + evento.getNome() + ": " + e.getMessage());
        e.printStackTrace();
        
        // Tentar placeholder em caso de erro
        try {
            java.io.InputStream placeholderStream = getClass().getResourceAsStream("/img/placeholder.png");
            if (placeholderStream != null) {
                imageView.setImage(new Image(placeholderStream));
            }
        } catch (Exception ex) {
            // Silenciar erro de placeholder
        }
    }

    Label statusLabel = new Label(evento.getStatus());
    statusLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
    statusLabel.setTextFill(Color.WHITE);
    statusLabel.setPadding(new Insets(3, 7, 3, 7));
    String statusStyle = "-fx-background-radius: 10; -fx-background-color: ";
    
    switch (evento.getStatus().toLowerCase()) {
        case "ativo" -> statusStyle += "#28a745;";
        case "aguardando aprovação" -> statusStyle += "#ffc107;";
        case "rejeitado", "cancelado" -> statusStyle += "#dc3545;";
        default -> statusStyle += "#6c757d;";
    }
    statusLabel.setStyle(statusStyle);

    // Criar container para imagem com centralização
    StackPane imagemContainer = new StackPane(imageView);
    imagemContainer.setAlignment(Pos.CENTER);
    
    // Criar container completo com imagem e status
    StackPane imagemComStatusPane = new StackPane(imagemContainer, statusLabel);
    StackPane.setAlignment(statusLabel, Pos.TOP_RIGHT);
    StackPane.setMargin(statusLabel, new Insets(8, 8, 0, 0));

    return imagemComStatusPane;
}

    @FXML
    private void handleSolicitarNovoEvento() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/ufms/eventos/view/TelaSolicitacaoEvento.fxml"));
            Stage stage = (Stage) btnSolicitarNovoEvento.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navegarParaDetalhes(MouseEvent event, Long eventoId) {
        if (eventoId == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "ID do evento é nulo. Não é possível navegar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/EventoDetalhado.fxml"));
            Parent root = loader.load();
            DetalhesEventoFXMLController controller = loader.getController();
            controller.carregarDadosDoEvento(eventoId);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Detalhes do Evento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
    Alert alert = new Alert(tipo);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(conteudo);

    // Usa um dos containers de cards para encontrar a janela "dona" do alerta.
    // Lembre-se de usar o nome correto da sua variável (@FXML private FlowPane disponiveisContainer;)
    if (disponiveisContainer != null && disponiveisContainer.getScene() != null) {
        alert.initOwner(disponiveisContainer.getScene().getWindow());
    }

    alert.showAndWait();
}
}