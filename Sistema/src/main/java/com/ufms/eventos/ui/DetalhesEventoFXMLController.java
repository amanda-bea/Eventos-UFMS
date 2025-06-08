package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AcaoController;
import com.ufms.eventos.controller.AdminController;
import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.AcaoMinDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
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
    }

    /**
     * Ponto de entrada desta tela. Recebe o ID do evento e carrega todos os seus dados.
     */
    public void carregarDadosDoEvento(Long eventoId) {
        this.eventoIdAtual = eventoId;
        try {
            EventoDTO evento = eventoController.buscarDtoPorId(eventoId);

            // Lógica de visibilidade dos botões do admin
            Usuario usuarioLogado = SessaoUsuario.getInstancia().getUsuarioLogado();
            if (usuarioLogado instanceof Admin && "Aguardando aprovação".equalsIgnoreCase(evento.getStatus())) {
                painelAcoesAdmin.setVisible(true);
                painelAcoesAdmin.setManaged(true);
            } else {
                painelAcoesAdmin.setVisible(false);
                painelAcoesAdmin.setManaged(false);
            }

            // Popula as informações principais do evento na UI
            popularInfoPrincipais(evento);

            // Busca as ações JÁ COM OS AVISOS de vagas e popula a lista na UI
            List<AcaoMinDTO> acoes = acaoController.listarAcoesPorEventoComAvisos(evento.getId());
            popularAcoes(acoes);

        } catch (IllegalArgumentException e) {
            nomeEventoLabel.setText("Evento não encontrado");
            descricaoLabel.setText("O evento com o ID " + eventoId + " não existe ou foi removido.");
            System.err.println(e.getMessage());
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
            if (evento.getImagem() != null && !evento.getImagem().isEmpty()) {
                bannerImageView.setImage(new Image(evento.getImagem(), true));
            } else { throw new Exception("URL da imagem vazia."); }
        } catch (Exception e) {
            bannerImageView.setImage(new Image(getClass().getResourceAsStream("/img/placeholder.png")));
        }
    }

    /**
     * Limpa o container de ações e adiciona um "card" para cada ação da lista.
     */
    private void popularAcoes(List<AcaoMinDTO> acoes) {
        acoesContainerVBox.getChildren().clear();
        if (acoes.isEmpty()) {
            acoesContainerVBox.getChildren().add(new Label("Nenhuma ação programada para este evento."));
            return;
        }
        for (AcaoMinDTO acao : acoes) {
            acoesContainerVBox.getChildren().add(criarCardAcao(acao));
        }
    }
    
    /**
     * Cria um painel HBox estilizado para representar uma única ação.
     * Este método agora inclui a lógica para exibir a label de "Últimas Vagas".
     */
    private HBox criarCardAcao(AcaoMinDTO acao) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(10));
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        ImageView acaoImageView = new ImageView();
        acaoImageView.setFitHeight(50);
        acaoImageView.setFitWidth(50);
        // ... (lógica de carregar imagem da ação)

        VBox infoBox = new VBox(3);
        Label nomeAcaoLabel = new Label(acao.getNome());
        nomeAcaoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label departamentoLabel = new Label("Departamento: " + acao.getDepartamento());
        departamentoLabel.setTextFill(Color.rgb(100, 100, 100));
        Label dataLabel = new Label("Data: " + acao.getData());
        dataLabel.setTextFill(Color.rgb(100, 100, 100));
        infoBox.getChildren().addAll(nomeAcaoLabel, departamentoLabel, dataLabel);
        
        // --- LÓGICA PARA EXIBIR A LABEL VERMELHA ---
        if (acao.getAvisoVagas() != null && !acao.getAvisoVagas().isEmpty()) {
            Label avisoLabel = new Label(acao.getAvisoVagas());
            avisoLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
            avisoLabel.setTextFill(Color.RED);
            avisoLabel.setPadding(new Insets(4, 0, 0, 0)); // Espaçamento superior
            infoBox.getChildren().add(avisoLabel); // Adiciona o aviso abaixo das outras infos
        }

        Button btnVerMais = new Button("Ver Detalhes");
        btnVerMais.setCursor(Cursor.HAND);
        btnVerMais.setStyle("-fx-background-color: #489ec1; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVerMais.setOnAction(e -> System.out.println("Clicou em Ver Detalhes da ação com ID: " + acao.getId()));

        javafx.scene.layout.Region separator = new javafx.scene.layout.Region();
        HBox.setHgrow(separator, javafx.scene.layout.Priority.ALWAYS);

        card.getChildren().addAll(acaoImageView, infoBox, separator, btnVerMais);
        return card;
    }
    
    // --- Métodos de Ação (Admin e Link) ---

    @FXML
    private void handleAprovar() {
        if (adminController.aprovarEvento(this.eventoIdAtual)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "O evento foi aprovado e agora está ativo.");
            fecharJanela();
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
                fecharJanela();
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