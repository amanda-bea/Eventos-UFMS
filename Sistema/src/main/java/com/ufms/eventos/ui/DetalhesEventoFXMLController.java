package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AcaoController;
import com.ufms.eventos.controller.AdminController;
import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Node;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetalhesEventoFXMLController implements Initializable {

    // --- CAMPOS FXML DA TELA ---
    @FXML private ImageView bannerImageView;
    @FXML private Label nomeEventoLabel;
    @FXML private Label datasLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label departamentoLabel;
    @FXML private Label descricaoLabel;
    @FXML private Hyperlink infoHyperlink;
    @FXML private VBox acoesContainerVBox;
    @FXML private HBox painelAcoesAdmin;
    @FXML private Button btnAprovar;
    @FXML private Button btnRejeitar;
    @FXML private StackPane imagemContainer;
    

    // --- CONTROLLERS DE LÓGICA ---
    private EventoController eventoController;
    private AcaoController acaoController;
    private AdminController adminController;
    
    private Long eventoIdAtual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.eventoController = new EventoController();
        this.acaoController = new AcaoController();
        this.adminController = new AdminController();
        // Configura tamanho e posicionamento ideal para a imagem
        // Configuração responsiva para a imagem
        Platform.runLater(() -> {
            // Garante que o container tenha altura fixa
            double headerHeight = 250.0;
            imagemContainer.setPrefHeight(headerHeight);
            imagemContainer.setMinHeight(headerHeight);
            
            // Configura o ImageView para ocupar o StackPane adequadamente
            bannerImageView.fitWidthProperty().bind(imagemContainer.widthProperty());
            bannerImageView.setFitHeight(headerHeight);
            
            // Adiciona listener para ajustar em tela cheia
            configureFullscreenHandling();
        });
    }

    private void configureFullscreenHandling() {
        Scene scene = imagemContainer.getScene();
        if (scene != null) {
            Stage stage = (Stage) scene.getWindow();
            
            // Listener para tela cheia
            stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) { // Está em tela cheia
                    // Ajusta a altura para melhor proporção em tela cheia
                    double screenHeight = Screen.getPrimary().getBounds().getHeight() * 0.3;
                    imagemContainer.setPrefHeight(screenHeight);
                    bannerImageView.setFitHeight(screenHeight);
                } else {
                    // Volta ao tamanho normal quando sair da tela cheia
                    imagemContainer.setPrefHeight(250);
                    bannerImageView.setFitHeight(250);
                }
            });
        }
    }

    /**
     * Ponto de entrada desta tela. Recebe o ID do evento e carrega todos os seus dados.
     */
    public void carregarDadosDoEvento(Long eventoId) {
        System.out.println("DetalhesEventoFXMLController.carregarDadosDoEvento recebeu ID: " + eventoId);

        if (eventoId == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "ID do evento não foi fornecido.");
            return;
        }
        
        try {
            this.eventoIdAtual = eventoId;
            EventoDTO evento = eventoController.buscarDtoPorId(eventoId);
            
            // Verifica se o evento foi encontrado
            if (evento == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Evento não encontrado na base de dados.");
                return;
            }

            // Configura a visibilidade das ações baseado no status do evento
            configurarAcoesPorStatusEvento(evento);

            // Popula as informações principais do evento na UI
            popularInfoPrincipais(evento);

            System.out.println("Antes de chamar listarAcoesPorEventoComAvisos, ID é: " + this.eventoIdAtual);
            List<AcaoDTO> acoes = acaoController.listarAcoesPorEventoComAvisos(this.eventoIdAtual);
            popularAcoes(acoes);
            
            // Chama novamente para verificar os botões de inscrição após popular as ações
            configurarAcoesPorStatusEvento(evento);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", 
                    "Ocorreu um erro ao carregar os dados do evento: " + e.getMessage());
        }
    }

    /**
     * Preenche os campos principais da tela com os dados do EventoDTO.
     */
    private void popularInfoPrincipais(EventoDTO evento) {
        nomeEventoLabel.setText(evento.getNome());
        datasLabel.setText(evento.getDataInicio() + " a " + evento.getDataFim());
        categoriaLabel.setText(evento.getCategoria());
        departamentoLabel.setText(evento.getDepartamento());
        descricaoLabel.setText(evento.getDescricao());

        // Configura o Hyperlink
        if (evento.getLink() != null && !evento.getLink().trim().isEmpty()) {
            infoHyperlink.setText(evento.getLink());
            infoHyperlink.setDisable(false);
        } else {
            infoHyperlink.setText("Nenhum link fornecido");
            infoHyperlink.setDisable(true);
        }

        // Carrega a imagem do banner
        try {
            String imagePath = evento.getImagem();
            if (imagePath != null && !imagePath.isEmpty()) {
                File arquivoImagem = new File(imagePath);
                
                if (arquivoImagem.exists()) {
                    // Carrega a imagem
                    Image image = new Image(new FileInputStream(arquivoImagem));
                    
                    // Aplica a imagem e configura para display apropriado
                    bannerImageView.setImage(image);
                    
                    // Ajusta o modo de visualização para melhor posicionamento
                    if (image.getWidth() / image.getHeight() > 1.5) {
                        // Para imagens muito largas, foca no centro
                        bannerImageView.setViewport(new Rectangle2D(
                            (image.getWidth() - image.getHeight() * 1.5) / 2, 
                            0, 
                            image.getHeight() * 1.5, 
                            image.getHeight()
                        ));
                    } else {
                        // Para imagens normais ou verticais, mostra tudo preservando proporção
                        bannerImageView.setViewport(null);
                    }
                    
                    // Garante que a imagem preencha o espaço disponível
                    bannerImageView.setPreserveRatio(true);
                } else {
                    System.err.println("Imagem não encontrada no caminho: " + imagePath);
                    carregarImagemPlaceholder();
                }
            } else {
                carregarImagemPlaceholder();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + e.getMessage());
            carregarImagemPlaceholder();
        }
    }
    /**
     * Configura a visibilidade dos botões de ação baseado no status do evento e tipo de usuário
     */
    private void configurarAcoesPorStatusEvento(EventoDTO evento) {
        Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
        boolean isAdmin = usuarioLogado instanceof Admin;
        
        // Configuração para painel de ações admin (aprovar/rejeitar)
        if (isAdmin && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            painelAcoesAdmin.setVisible(true);
            painelAcoesAdmin.setManaged(true);
        } else {
            painelAcoesAdmin.setVisible(false);
            painelAcoesAdmin.setManaged(false);
        }
        
        // Oculta opções de inscrição para eventos pendentes
        if ("Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            // Percorre todas as ações e desativa botões de inscrição
            for (Node node : acoesContainerVBox.getChildren()) {
                if (node instanceof VBox) {
                    VBox acaoBox = (VBox) node;
                    for (Node child : acaoBox.getChildren()) {
                        if (child instanceof Button) {
                            Button btn = (Button) child;
                            if (btn.getText().contains("Inscrever") || btn.getText().contains("inscrição")) {
                                btn.setVisible(false);
                                btn.setManaged(false);
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Carrega uma imagem placeholder quando a imagem principal não está disponível
     */
    private void carregarImagemPlaceholder() {
        try {
            // Tenta carregar o placeholder de recursos
            InputStream is = getClass().getResourceAsStream("/img/placeholder.png");
            if (is != null) {
                bannerImageView.setImage(new Image(is));
            } else {
                System.err.println("Arquivo placeholder.png não encontrado nos recursos");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem placeholder: " + e.getMessage());
        }
    }
    
    private void popularAcoes(List<AcaoDTO> acoes) {
        acoesContainerVBox.getChildren().clear();

        if (acoes.isEmpty()) {
            Label msgLabel = new Label("Nenhuma ação programada para este evento.");
            msgLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");
            acoesContainerVBox.getChildren().add(msgLabel);
            return;
        }
        
        // Verifica o status do evento
        EventoDTO evento = eventoController.buscarDtoPorId(this.eventoIdAtual);
        
        // Adiciona um label explicativo para eventos pendentes
        if (evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
            Label statusLabel = new Label("Evento aguardando aprovação - Inscrições serão liberadas após aprovação");
            statusLabel.setStyle("-fx-background-color: #fff3cd; -fx-padding: 5 10; -fx-text-fill: #856404; " + 
                                "-fx-border-color: #ffeeba; -fx-border-radius: 3; -fx-background-radius: 3;");
            acoesContainerVBox.getChildren().add(statusLabel);
            VBox.setMargin(statusLabel, new Insets(0, 0, 10, 0));
        }

        // Cria os cards para as ações
        for (AcaoDTO acao : acoes) {
            // Cria um painel expansível para cada ação
            acoesContainerVBox.getChildren().add(criarCardAcaoExpansivel(acao));
        }
    }
    
    /**
     * NOVO MÉTODO: Cria um TitledPane expansível para uma única ação.
     * @param acao A ação para exibir
     * @return Um TitledPane configurado com os detalhes da ação
     */
    private TitledPane criarCardAcaoExpansivel(AcaoDTO acao) {
        // O Título do painel mostra o nome e a data da ação
        String titulo = acao.getNome() + "  -  " + acao.getData();
        TitledPane titledPane = new TitledPane(titulo, null);
        titledPane.setFont(Font.font("System", FontWeight.BOLD, 14));
        titledPane.setExpanded(false); // Começa fechado
        
        // --- O Conteúdo que aparece ao expandir ---
        // Usamos um GridPane para alinhar as informações de forma bonita
        GridPane gridConteudo = new GridPane();
        gridConteudo.setHgap(10);
        gridConteudo.setVgap(8);
        gridConteudo.setPadding(new Insets(15, 10, 10, 10));

        // Adiciona as informações detalhadas ao grid
        gridConteudo.add(new Label("Descrição:"), 0, 0);
        Label descLabel = new Label(acao.getDescricao());
        descLabel.setWrapText(true);
        gridConteudo.add(descLabel, 1, 0);

        gridConteudo.add(new Label("Horário:"), 0, 1);
        gridConteudo.add(new Label(acao.getHorarioInicio() + " - " + acao.getHorarioFim()), 1, 1);
        
        gridConteudo.add(new Label("Local:"), 0, 2);
        gridConteudo.add(new Label(acao.getLocal()), 1, 2);
        
        gridConteudo.add(new Label("Departamento:"), 0, 3);
        gridConteudo.add(new Label(acao.getDepartamento()), 1, 3);
        
        gridConteudo.add(new Label("Modalidade:"), 0, 4);
        gridConteudo.add(new Label(acao.getModalidade()), 1, 4);

        gridConteudo.add(new Label("Capacidade:"), 0, 5);
        String capacidade = "Ilimitada";
        if (acao.getCapacidade() != null && !acao.getCapacidade().equals("0")) {
            capacidade = acao.getCapacidade() + " vagas";
        }
        gridConteudo.add(new Label(capacidade), 1, 5);

        gridConteudo.add(new Label("Contato:"), 0, 6);
        gridConteudo.add(new Label(acao.getContato()), 1, 6);

        // Define o grid como o conteúdo do painel expansível
        titledPane.setContent(gridConteudo);

        // Verifica o status do evento antes de adicionar o botão de inscrição
        EventoDTO evento = eventoController.buscarDtoPorId(this.eventoIdAtual);
        boolean eventoPendente = evento != null && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus());
        
        // Só adiciona o botão se o evento não estiver pendente
        if (!eventoPendente) {
            // Adiciona o botão "Inscrever-se" ao final do conteúdo
            Button btnInscrever = new Button("Inscrever-se nesta Ação");
            btnInscrever.setStyle("-fx-background-color: #489ec1; -fx-text-fill: white; -fx-font-weight: bold;");
            btnInscrever.setOnAction(e -> handleInscrever(acao));
            
            // Adiciona o botão ao grid de conteúdo
            gridConteudo.add(btnInscrever, 0, 7, 2, 1); // Ocupa 2 colunas e 1 linha
        } else {
            // Adiciona uma mensagem informativa para eventos pendentes
            Label pendingLabel = new Label("Inscrições disponíveis após aprovação do evento");
            pendingLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            gridConteudo.add(pendingLabel, 0, 7, 2, 1);
        }
        
        return titledPane;
    }

    private void handleInscrever(AcaoDTO acao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/FormularioInscricao.fxml"));
            Parent root = loader.load();

            // Pega o controller da tela de formulário
            FormularioInscricaoFXMLController formController = loader.getController();
            // Passa os dados da ação para ele saber qual formulário montar
            formController.initData(acao);

            Stage formStage = new Stage();
            formStage.setTitle("Formulário de Inscrição");
            formStage.setScene(new Scene(root));
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.initOwner(acoesContainerVBox.getScene().getWindow());
            formStage.showAndWait();

            System.out.println("Janela de formulário fechada. Atualizando detalhes do evento...");
            carregarDadosDoEvento(this.eventoIdAtual);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    // --- Métodos de Ação (Admin e Link) ---

    @FXML
    private void handleAprovar() {
        if (adminController.aprovarEvento(this.eventoIdAtual)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "O evento foi aprovado e agora está ativo.");
            // Recarregar os dados para atualizar a interface
            carregarDadosDoEvento(this.eventoIdAtual);
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível aprovar o evento.");
        }
    }

    @FXML
    private void handleRejeitar() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rejeitar Evento");
        dialog.setHeaderText("Rejeitando o evento ID: " + this.eventoIdAtual);
        dialog.setContentText("Por favor, informe o motivo da rejeição:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(motivo -> {
            if (motivo.trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Inválido", "O motivo não pode estar vazio.");
                return;
            }
            if (adminController.rejeitarEvento(this.eventoIdAtual, motivo)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "O evento foi rejeitado.");
                // Recarregar os dados para atualizar a interface
                carregarDadosDoEvento(this.eventoIdAtual);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível rejeitar o evento.");
            }
        });
    }
    
    @FXML
    private void handleLinkClick() {
        String url = infoHyperlink.getText();
        if (url == null || url.equals("Nenhum link fornecido")) return;

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            System.err.println("Erro ao tentar abrir o link: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Link Inválido", "Não foi possível abrir o endereço: " + url);
        }
    }

    private void fecharJanela() {
        if (btnAprovar != null && btnAprovar.getScene() != null) {
            ((Stage) btnAprovar.getScene().getWindow()).close();
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}