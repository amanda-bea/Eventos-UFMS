package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AcaoController;
import com.ufms.eventos.controller.AdminController;
import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.controller.PresencaConfirmadaController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.awt.Desktop;

public class DetalhesEventoFXMLController implements Initializable {

    // --- CAMPOS FXML ---
    @FXML private ImageView bannerImageView;
    @FXML private Label nomeEventoLabel;
    @FXML private Label datasLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label departamentoLabel;
    @FXML private Label organizadorLabel;
    @FXML private Label descricaoLabel;
    @FXML private Hyperlink infoHyperlink;
    @FXML private VBox acoesContainerVBox;
    @FXML private HBox painelAcoesAdmin;
    @FXML private Button btnAprovar;
    @FXML private Button btnRejeitar;
    @FXML private StackPane imagemContainer;
    @FXML private HomebarFXMLController homebarController;

    // --- CONTROLLERS DE LÓGICA ---
    private EventoController eventoController;
    private AcaoController acaoController;
    private AdminController adminController;
    private PresencaConfirmadaController presencaController;
    
    private Long eventoIdAtual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        this.acaoController = new AcaoController();
        this.adminController = new AdminController();
        this.presencaController = new PresencaConfirmadaController();
        
        // Configura o clipping da imagem para bordas arredondadas
        if (imagemContainer != null) {
            Rectangle clip = new Rectangle();
            clip.setArcWidth(15.0);
            clip.setArcHeight(15.0);
            clip.widthProperty().bind(imagemContainer.widthProperty());
            clip.heightProperty().bind(imagemContainer.heightProperty());
            imagemContainer.setClip(clip);
        }
        
        if (homebarController != null) {
            homebarController.configurarParaPagina("DETALHES");
        }

        bannerImageView.setPreserveRatio(false);
        bannerImageView.setSmooth(true); // Melhora a qualidade visual
        bannerImageView.setCache(true);
    }

    public void carregarDadosDoEvento(Long eventoId) {
        this.eventoIdAtual = eventoId;
        try {
            EventoDTO eventoDTO = eventoController.buscarDtoPorId(eventoId);
            List<AcaoDTO> acoes = acaoController.listarAcoesCompletasPorEvento(eventoId);

            // Popula todas as partes da UI com os dados buscados
            popularInfoPrincipais(eventoDTO);
            popularAcoes(acoes, eventoDTO);
            configurarPaineisAdmin(eventoDTO);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível carregar os detalhes do evento.");
        }
    }

    private void popularInfoPrincipais(EventoDTO evento) {
        nomeEventoLabel.setText(evento.getNome());
        organizadorLabel.setText(evento.getOrganizador());
        datasLabel.setText("De " + evento.getDataInicio() + " a " + evento.getDataFim());
        categoriaLabel.setText(evento.getCategoria());
        departamentoLabel.setText(evento.getDepartamento());
        descricaoLabel.setText(evento.getDescricao());

        if (evento.getLink() != null && !evento.getLink().trim().isEmpty()) {
            infoHyperlink.setText(evento.getLink());
            infoHyperlink.setDisable(false);
        } else {
            infoHyperlink.setText("Nenhum link fornecido");
            infoHyperlink.setDisable(true);
        }

        try {
            String imagePath = evento.getImagem();
            Image image = null;
            if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
                image = new Image(new FileInputStream(imagePath));
            } else {
                InputStream is = getClass().getResourceAsStream("/img/placeholder.png");
                if (is != null) image = new Image(is);
            }
            bannerImageView.setImage(image);
            bannerImageView.setFitWidth(800);
            bannerImageView.setFitHeight(250);
            bannerImageView.setPreserveRatio(false);

        } catch (Exception e) {
            System.err.println("Falha ao carregar imagem para o evento '" + evento.getNome() + "'.");
            e.printStackTrace();
        }
    }

    private void popularAcoes(List<AcaoDTO> acoes, EventoDTO eventoDTO) {
        acoesContainerVBox.getChildren().clear();
        if (acoes.isEmpty()) {
            acoesContainerVBox.getChildren().add(new Label("Nenhuma ação programada para este evento."));
            return;
        }

        // Se o evento estiver pendente, mostra um aviso geral no topo da lista de ações
        if ("Aguardando aprovação".equalsIgnoreCase(eventoDTO.getStatus())) {
            Label statusAviso = new Label("As inscrições serão liberadas após a aprovação do evento pelo administrador.");
            statusAviso.setStyle("-fx-background-color: #fff3cd; -fx-padding: 8; -fx-text-fill: #856404; -fx-border-color: #ffeeba; -fx-background-radius: 4; -fx-border-radius: 4;");
            acoesContainerVBox.getChildren().add(statusAviso);
        }
        
        for (AcaoDTO acao : acoes) {
            acoesContainerVBox.getChildren().add(criarCardAcaoExpansivel(acao, eventoDTO));
        }
    }

    private TitledPane criarCardAcaoExpansivel(AcaoDTO acao, EventoDTO evento) {
        // O título do painel mostra o nome e a data da ação
        String titulo = acao.getNome() + "  -  " + acao.getData();
        TitledPane titledPane = new TitledPane(titulo, null);
        titledPane.setFont(Font.font("System", FontWeight.BOLD, 14));
        titledPane.setExpanded(false); // O painel começa fechado
        titledPane.setAnimated(true); // Deixa a animação de expandir suave

        // conteudo interno (um grid para alinhar tudo)
        GridPane gridConteudo = new GridPane();
        gridConteudo.setHgap(10);
        gridConteudo.setVgap(8);
        gridConteudo.setPadding(new Insets(15, 10, 10, 20));

        // populando grid
        int linhaAtual = 0;
        gridConteudo.add(new Label("Descrição:"), 0, linhaAtual);
        Label descLabel = new Label(acao.getDescricao() != null && !acao.getDescricao().isEmpty() ? acao.getDescricao() : "Nenhuma descrição fornecida.");
        descLabel.setWrapText(true);
        gridConteudo.add(descLabel, 1, linhaAtual++);

        gridConteudo.add(new Label("Horário:"), 0, linhaAtual);
        gridConteudo.add(new Label(acao.getHorarioInicio() + " às " + acao.getHorarioFim()), 1, linhaAtual++);
        
        gridConteudo.add(new Label("Local:"), 0, linhaAtual);
        gridConteudo.add(new Label(acao.getLocal()), 1, linhaAtual++);
        
        gridConteudo.add(new Label("Modalidade:"), 0, linhaAtual);
        gridConteudo.add(new Label(acao.getModalidade()), 1, linhaAtual++);

        String capacidade = "Ilimitada";
        if (acao.getCapacidade() != null && !acao.getCapacidade().equals("0")) {
            capacidade = acao.getCapacidade() + " vagas";
        }
        gridConteudo.add(new Label("Capacidade:"), 0, linhaAtual);
        gridConteudo.add(new Label(capacidade), 1, linhaAtual++);

        // botões
        VBox containerBotao = new VBox(5); // Um container para o botão e a futura lista de inscritos
        Button actionButton;
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();

        // O usuário logado é o organizador do evento
        if (usuarioLogado != null && evento.getOrganizador().equals(usuarioLogado.getNome())) {
            actionButton = new Button("Ver Inscritos");
            actionButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
            actionButton.setOnAction(e -> handleVerInscritos(acao, containerBotao, actionButton));
        
        } else { //um usuário comum
            boolean inscrito = (usuarioLogado != null) && presencaController.isUsuarioInscrito(usuarioLogado, acao.getId());

            if (inscrito) {
                actionButton = new Button("Cancelar Inscrição");
                actionButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
                actionButton.setOnAction(e -> handleCancelarInscricao(acao));
            } else {
                actionButton = new Button("Inscrever-se nesta Ação");
                actionButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
                actionButton.setOnAction(e -> handleInscrever(acao));

                if ("Lotado".equalsIgnoreCase(acao.getStatus())) {
                    actionButton.setDisable(true);
                    actionButton.setText("Esgotado");
                    actionButton.setStyle("-fx-background-color: #777777; -fx-text-fill: white;");
                }
            }
        }
        
        //Desabilita o botão se o evento inteiro ainda estiver pendente
        if ("Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            actionButton.setDisable(true);
            // Opcional: Adicionar uma mensagem
            Label avisoPendente = new Label("Inscrições indisponíveis até a aprovação do evento.");
            avisoPendente.setStyle("-fx-text-fill: #856404; -fx-font-style: italic;");
            containerBotao.getChildren().add(avisoPendente);
        }
        
        containerBotao.getChildren().add(0, actionButton);
        gridConteudo.add(containerBotao, 0, linhaAtual, 2, 1);
        titledPane.setContent(gridConteudo);
        
        return titledPane;
    }

    private void configurarPaineisAdmin(EventoDTO evento) {
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (usuarioLogado instanceof Admin && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            painelAcoesAdmin.setVisible(true);
            painelAcoesAdmin.setManaged(true);
        } else {
            painelAcoesAdmin.setVisible(false);
            painelAcoesAdmin.setManaged(false);
        }
    }

    private void handleVerInscritos(AcaoDTO acao, VBox containerBotao, Button botao) {
        Node listaExistente = containerBotao.lookup("#lista-inscritos");
        if (listaExistente != null) {
            containerBotao.getChildren().remove(listaExistente);
            botao.setText("Ver Inscritos");
        } else {
            List<Usuario> inscritos = presencaController.listarInscritosPorAcao(acao.getId());
            botao.setText("Ocultar Inscritos (" + inscritos.size() + ")");

            VBox inscritosContainer = new VBox(5);
            inscritosContainer.setId("lista-inscritos");
            inscritosContainer.setPadding(new Insets(10, 0, 0, 10));
            
            if (inscritos.isEmpty()) {
                inscritosContainer.getChildren().add(new Label("Ninguém se inscreveu nesta ação ainda."));
            } else {
                for (Usuario inscrito : inscritos) {
                    inscritosContainer.getChildren().add(new Label("• " + inscrito.getNome()));
                }
            }
            containerBotao.getChildren().add(inscritosContainer);
        }
    }

    private void handleInscrever(AcaoDTO acao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/FormularioInscricao.fxml"));
            Parent root = loader.load();
            FormularioInscricaoFXMLController formController = loader.getController();
            formController.initData(acao);
            
            Stage formStage = new Stage();
            formStage.initModality(Modality.WINDOW_MODAL);
            formStage.initOwner(acoesContainerVBox.getScene().getWindow());
            formStage.setScene(new Scene(root));
            formStage.showAndWait();
            
            carregarDadosDoEvento(this.eventoIdAtual);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCancelarInscricao(AcaoDTO acao) {
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        Alert confirmacao = new Alert(AlertType.CONFIRMATION, "Deseja cancelar sua inscrição em '" + acao.getNome() + "'?", ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar Cancelamento");
        
        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                if (presencaController.cancelarInscricao(usuarioLogado, acao.getId())) {
                    mostrarAlerta(AlertType.INFORMATION, "Sucesso", "Sua inscrição foi cancelada.");
                    carregarDadosDoEvento(this.eventoIdAtual);
                } else {
                    mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível cancelar sua inscrição.");
                }
            }
        });
    }


    @FXML
    private void handleAprovar() {
        if (adminController.aprovarEvento(this.eventoIdAtual)) {
            mostrarAlerta(AlertType.INFORMATION, "Sucesso", "O evento foi aprovado.");
            carregarDadosDoEvento(this.eventoIdAtual);
        } else {
            mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível aprovar o evento.");
        }
    }

    @FXML
    private void handleRejeitar() {
        // Cria uma janela de diálogo para pedir o motivo da rejeição
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejeitar Evento");
        dialog.setHeaderText("Rejeitando o evento ID: " + this.eventoIdAtual);
        dialog.setContentText("Por favor, informe o motivo da rejeição:");
        dialog.initOwner(btnRejeitar.getScene().getWindow()); // Garante que o diálogo apareça sobre a janela principal

        Optional<String> resultado = dialog.showAndWait();

        resultado.ifPresent(motivo -> {
            if (motivo == null || motivo.trim().isEmpty()) {
                mostrarAlerta(AlertType.WARNING, "Inválido", "O motivo da rejeição não pode estar vazio.");
                return;
            }
            
            boolean sucesso = adminController.rejeitarEvento(this.eventoIdAtual, motivo.trim());
            
            if (sucesso) {
                mostrarAlerta(AlertType.INFORMATION, "Sucesso", "O evento foi rejeitado.");
                carregarDadosDoEvento(this.eventoIdAtual);
            } else {
                mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível rejeitar o evento.");
            }
        });
    }
    
    @FXML
    private void handleLinkClick() {
        String url = infoHyperlink.getText();

        if (url == null || url.trim().isEmpty() || url.equals("Nenhum link fornecido")) {
            System.out.println("Nenhum link para abrir.");
            return;
        }

        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
            url = "http://" + url;
        }

        try {
            
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                mostrarAlerta(AlertType.WARNING, "Ação não suportada", "Seu sistema operacional não suporta a abertura de links a partir de aplicativos Java.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Link Inválido", "Não foi possível abrir o endereço: " + url);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
    Alert alert = new Alert(tipo);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(conteudo);

    // Usa o label do nome do evento para encontrar a janela "dona" do alerta.
    if (nomeEventoLabel != null && nomeEventoLabel.getScene() != null) {
        alert.initOwner(nomeEventoLabel.getScene().getWindow());
    }

    alert.showAndWait();
}
}