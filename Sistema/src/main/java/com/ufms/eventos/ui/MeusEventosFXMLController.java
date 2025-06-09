package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoMinDTO;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller para a tela "Meus Eventos" do Organizador.
 * Esta classe é autossuficiente, buscando o usuário logado diretamente da SessaoUsuario.
 */
public class MeusEventosFXMLController implements Initializable {

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
    
    private AnchorPane criarCardMeuEvento(EventoMinDTO evento) {
        // Adicione a verificação de segurança AQUI
        if (evento.getId() == null) {
            System.err.println("AVISO: Evento '" + evento.getNome() + "' tem ID nulo!");
        } else {
            System.out.println("Criando card para evento '" + evento.getNome() + "' (ID: " + evento.getId() + ")");
        }
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
            String imagePath = evento.getImagem();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                File arquivoImagem = new File(imagePath);
                
                if (arquivoImagem.exists()) {
                    imageView.setImage(new Image(new FileInputStream(arquivoImagem)));
                } else {
                    // Se o arquivo do evento não for encontrado, carrega o SEU placeholder
                    System.err.println("Imagem do evento não encontrada em: " + imagePath);
                    // ** MUDANÇA AQUI **
                    imageView.setImage(new Image(new FileInputStream("Sistema/imagens_eventos/placeholder.png")));
                }
            } else {
                // Se não houver imagem no banco, carrega o SEU placeholder
                // ** MUDANÇA AQUI **
                imageView.setImage(new Image(new FileInputStream("Sistema/imagens_eventos/placeholder.png")));
            }
        } catch (Exception e) {
            // Em caso de qualquer erro, também carrega o SEU placeholder
            System.err.println("Falha geral ao carregar imagem. Usando placeholder. Erro: " + e.getMessage());
            try {
                // ** MUDANÇA AQUI **
                imageView.setImage(new Image(new FileInputStream("Sistema/imagens_eventos/placeholder.png")));
            } catch (Exception ex) {
                // Caso nem o placeholder seja encontrado
                System.err.println("ERRO CRÍTICO: Imagem placeholder também não encontrada!");
                ex.printStackTrace();
            }
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
                // Chama o método de navegação, passando o ID do evento e o evento do mouse
                navegarParaDetalhes(evento.getId(), mouseEvent);
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

    /**
     * Lógica para navegar para a tela de detalhes do evento.
     * @param eventoId O ID do evento para carregar na próxima tela.
     * @param mouseEvent O evento de clique, usado para obter a janela atual.
     */
    /**
     * Navega para a tela de detalhes do evento quando o card é clicado.
     */
    private void navegarParaDetalhes(Long eventoId, MouseEvent event) {
        try {
            if (eventoId == null) {
                System.err.println("ERRO: Não é possível navegar para detalhes - ID do evento é nulo");
                mostrarAlerta("Erro", "Evento inválido", 
                            "Não foi possível abrir os detalhes pois o ID do evento é inválido.", 
                            AlertType.ERROR);
                return;
            }
            
            System.out.println("Navegando para detalhes do evento ID: " + eventoId);
            
            // Define o caminho correto do FXML
            URL fxmlUrl = getClass().getResource("/com/ufms/eventos/view/EventoDetalhado.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERRO: Arquivo FXML não encontrado em: /com/ufms/eventos/view/DetalhesEvento.fxml");
                mostrarAlerta("Erro", "Arquivo não encontrado", 
                            "O arquivo de interface para detalhes do evento não foi encontrado.", 
                            AlertType.ERROR);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // Passa o ID para o controller da tela de detalhes
            DetalhesEventoFXMLController controller = loader.getController();
            controller.carregarDadosDoEvento(eventoId);
            
            // Cria e configura a nova cena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Detalhes do Evento");
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Erro ao navegar para detalhes: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha na navegação", 
                        "Não foi possível abrir a tela de detalhes: " + e.getMessage(), 
                        AlertType.ERROR);
        }
    }

    /**
     * Exibe um alerta para o usuário.
     */
    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }
}