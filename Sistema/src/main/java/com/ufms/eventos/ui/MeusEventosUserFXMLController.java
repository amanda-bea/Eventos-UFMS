package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.stream.Collectors;

/**
 * Controller para a tela "Meus Eventos" do Organizador.
 * Esta classe é autossuficiente, buscando o usuário logado diretamente da SessaoUsuario.
 */
public class MeusEventosUserFXMLController implements Initializable {

    @FXML
    private TilePane eventoContainer;

    @FXML
    private Label tituloPrincipalLabel;

    @FXML
    private Button btnSolicitarNovoEvento;

    private EventoController eventoController;

    /**
     * O método initialize é chamado automaticamente quando o FXML é carregado.
     * É o ponto de entrada perfeito para buscar o usuário da sessão e carregar os dados.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        
        // Pega o usuário DIRETAMENTE da sessão.
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (tituloPrincipalLabel != null) {
            tituloPrincipalLabel.setText("Meus Eventos");
        }
        
        // Dispara o carregamento dos eventos para o usuário da sessão.
        carregarMeusEventos(usuarioLogado);
    }

    /**
     * Carrega e exibe os eventos que pertencem ao usuário logado.
     * @param usuarioLogado O usuário obtido da sessão.
     */
    private void carregarMeusEventos(Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            eventoContainer.getChildren().add(new Label("Erro: Sessão de usuário inválida."));
            return;
        }

        eventoContainer.getChildren().clear();
        
        List<EventoMinDTO> todosMeusEventos = eventoController.buscarEventosPorUsuario(usuarioLogado);

        if (todosMeusEventos.isEmpty()) {
            Label msg = new Label("Você ainda não criou nenhum evento.");
            msg.setFont(Font.font("System", 14));
            eventoContainer.getChildren().add(msg);
            return;
        }

        // Separa os eventos em duas listas, conforme a regra
        List<EventoMinDTO> eventosAtivos = todosMeusEventos.stream()
                .filter(e -> "Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());

        List<EventoMinDTO> eventosInativos = todosMeusEventos.stream()
                .filter(e -> !"Ativo".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
        
        // Adiciona os cards dos eventos ativos
        if (!eventosAtivos.isEmpty()) {
            eventoContainer.getChildren().add(criarLabelSeparador("Eventos Ativos"));
            for (EventoMinDTO evento : eventosAtivos) {
                eventoContainer.getChildren().add(criarCardMeuEvento(evento));
            }
        }

        // Adiciona os cards dos eventos inativos
        if (!eventosInativos.isEmpty()) {
            eventoContainer.getChildren().add(criarLabelSeparador("Eventos Inativos e Pendentes"));
            for (EventoMinDTO evento : eventosInativos) {
                eventoContainer.getChildren().add(criarCardMeuEvento(evento));
            }
        }
    }
    
    /**
     * Cria o card visual para um evento, incluindo uma etiqueta de status colorida.
     */
    private AnchorPane criarCardMeuEvento(EventoMinDTO evento) {
        AnchorPane cardPane = new AnchorPane();
        cardPane.setPrefSize(177, 208);
        cardPane.setStyle("-fx-background-color: #e9f5fa; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        cardPane.setCursor(Cursor.HAND);

        // Imagem do Evento
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120.0);
        imageView.setFitWidth(160.0);
        imageView.setLayoutX(8.0);
        imageView.setLayoutY(8.0);
        try {
            imageView.setImage(new Image(evento.getImagem(), true));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/img/placeholder.png")));
        }

        // Textos: Nome, Categoria, Data
        Label nomeLabel = new Label(evento.getNome());
        nomeLabel.setLayoutX(10.0);
        nomeLabel.setLayoutY(135.0);
        nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label categoriaLabel = new Label(evento.getCategoria());
        categoriaLabel.setLayoutX(10.0);
        categoriaLabel.setLayoutY(155.0);
        categoriaLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        categoriaLabel.setTextFill(Color.web("#489ec1"));

        Label dataLabel = new Label(evento.getDataInicio());
        dataLabel.setLayoutX(10.0);
        dataLabel.setLayoutY(180.0);
        dataLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        
        // Etiqueta de Status
        Label statusLabel = new Label(evento.getStatus());
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setPadding(new Insets(2, 5, 2, 5));
        statusLabel.setStyle("-fx-background-radius: 5;");

        // Define a cor da etiqueta baseada no status
        switch (evento.getStatus().toLowerCase()) {
            case "ativo":
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #5cb85c;"); // Verde
                break;
            case "aguardando aprovação":
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #f0ad4e;"); // Laranja
                break;
            case "rejeitado":
            case "cancelado":
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #d9534f;"); // Vermelho
                break;
            default: // Inativo, etc.
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #777777;"); // Cinza
                break;
        }

        AnchorPane.setTopAnchor(statusLabel, 10.0);
        AnchorPane.setRightAnchor(statusLabel, 10.0);

        cardPane.getChildren().addAll(imageView, nomeLabel, categoriaLabel, dataLabel, statusLabel);

        cardPane.setOnMouseClicked(mouseEvent -> {
            System.out.println("Clicou no evento '" + evento.getNome() + "' com ID: " + evento.getId());
            // TODO: Implementar navegação para a tela de detalhes do evento, passando o ID.
        });

        return cardPane;
    }

    /**
     * Cria um Label que funciona como um separador de seção dentro do TilePane.
     */
    private Label criarLabelSeparador(String texto) {
        Label separador = new Label(texto);
        separador.setFont(Font.font("System", FontWeight.BOLD, 16));
        separador.setPadding(new Insets(10, 0, 5, 0));
        separador.setPrefWidth(540); // Largura aproximada do TilePane
        separador.setTextFill(Color.web("#2f4a51"));
        return separador;
    }

    /**
     * Navega para a tela de solicitação de evento.
     */
    @FXML
    private void handleSolicitarNovoEvento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/TelaSolicitacaoEvento.fxml"));
            Parent root = loader.load();

            // A tela de destino (SolicitarEventoFXMLController) também deve usar
            // a SessaoUsuario para pegar o usuário. Não precisamos mais passar dados via initData.
            
            Stage stage = (Stage) btnSolicitarNovoEvento.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.println("Falha ao carregar a tela de solicitação de evento.");
            e.printStackTrace();
        }
    }
}