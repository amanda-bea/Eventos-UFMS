package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AcaoController;
import com.ufms.eventos.controller.AdminController;
import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Admin;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
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
            List<AcaoDTO> acoes = acaoController.listarAcoesPorEventoComAvisos(evento.getId());
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

    private void popularAcoes(List<AcaoDTO> acoes) {
        acoesContainerVBox.getChildren().clear();

        if (acoes.isEmpty()) {
            acoesContainerVBox.getChildren().add(new Label("Nenhuma ação programada para este evento."));
            return;
        }

        for (AcaoDTO acao : acoes) {
            // Cria um painel expansível para cada ação
            acoesContainerVBox.getChildren().add(criarCardAcaoExpansivel(acao));
        }
    }
    
    /**
     * NOVO MÉTODO: Cria um TitledPane expansível para uma única ação.
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

        // Adiciona o botão "Inscrever-se" ao final do conteúdo
        Button btnInscrever = new Button("Inscrever-se nesta Ação");
        btnInscrever.setStyle("-fx-background-color: #489ec1; -fx-text-fill: white; -fx-font-weight: bold;");
        btnInscrever.setOnAction(e -> handleInscrever(acao));
        
        // Adiciona o botão ao grid de conteúdo
        // A coluna 0, e uma nova linha após a última informação (ex: linha 7)
        gridConteudo.add(btnInscrever, 0, 7, 2, 1); // Ocupa 2 colunas e 1 linha

        titledPane.setContent(gridConteudo);
        
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