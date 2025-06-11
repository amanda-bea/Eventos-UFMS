package com.ufms.eventos.ui;

import com.ufms.eventos.controller.ConfiguracaoFormularioController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DefinirFormularioFXMLController implements Initializable {

    // --- CAMPOS FXML DA TELA ---
    @FXML private Label lblTituloAcao;
    @FXML private CheckBox chkUsarNome;
    @FXML private CheckBox chkUsarEmail;
    @FXML private CheckBox chkUsarCpf;
    @FXML private CheckBox chkUsarCurso;
    @FXML private VBox camposPersonalizadosContainer;

    // --- DADOS E CONTROLLERS ---
    private AcaoDTO acaoAtual;
    private ConfiguracaoFormularioController configController;
    private List<TextField> camposPersonalizadosFields = new ArrayList<>();
    
    //avisar a tela anterior se a operação foi um sucesso
    private boolean foiSalvo = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.configController = new ConfiguracaoFormularioController();
        //valores padrão para os checkboxes
        chkUsarNome.setSelected(true);
        chkUsarEmail.setSelected(true);
    }

    /**
     * Ponto de entrada: recebe a Ação para a qual este formulário será definido.
     */
    public void initData(AcaoDTO acao) {
        this.acaoAtual = acao;
        lblTituloAcao.setText("Definir Formulário para: " + acao.getNome());
        
        // Tenta carregar uma configuração já existente para esta ação
        Optional<ConfiguracaoFormularioDTO> configOpt = configController.buscarConfiguracaoPorAcaoId(acao.getId());
        configOpt.ifPresent(this::popularFormularioExistente);
    }
    

    private void popularFormularioExistente(ConfiguracaoFormularioDTO config) {
        chkUsarNome.setSelected(config.isUsarNome());
        chkUsarEmail.setSelected(config.isUsarEmail());
        chkUsarCpf.setSelected(config.isUsarCpf());
        chkUsarCurso.setSelected(config.isUsarCurso());
        
        camposPersonalizadosContainer.getChildren().clear();
        camposPersonalizadosFields.clear();
        if (config.getCamposPersonalizados() != null) {
            config.getCamposPersonalizados().forEach(this::adicionarLinhaCampoPersonalizadoUI);
        }
    }

    /**
     * Chamado pelo botão "+ Adicionar Campo Personalizado".
     */
    @FXML
    private void handleAdicionarCampoPersonalizado() {
        adicionarLinhaCampoPersonalizadoUI("");
    }

    /**
     * Cria a UI para um novo campo personalizado.
     */
    private void adicionarLinhaCampoPersonalizadoUI(String nomeInicial) {
        HBox linhaCampo = new HBox(10);
        linhaCampo.setAlignment(Pos.CENTER_LEFT);
        
        TextField nomeCampoField = new TextField(nomeInicial);
        nomeCampoField.setPromptText("Nome do Campo");
        HBox.setHgrow(nomeCampoField, Priority.ALWAYS);
        camposPersonalizadosFields.add(nomeCampoField); // Adiciona na lista para ler depois

        Button btnRemover = new Button("Remover");
        btnRemover.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
        btnRemover.setOnAction(e -> {
            camposPersonalizadosContainer.getChildren().remove(linhaCampo);
            camposPersonalizadosFields.remove(nomeCampoField);
        });
        
        linhaCampo.getChildren().addAll(new Label("•"), nomeCampoField, btnRemover);
        camposPersonalizadosContainer.getChildren().add(linhaCampo);
    }
    
    @FXML
    private void handleFinalizarFormulario() {
        List<String> nomesCamposPersonalizados = new ArrayList<>();
        // Valida e coleta os campos personalizados
        for (TextField textField : camposPersonalizadosFields) {
            String nomeCampo = textField.getText().trim();
            if (nomeCampo.isEmpty()) {
                mostrarAlerta("Campo Inválido", "O nome de um campo personalizado não pode ser vazio.", AlertType.WARNING);
                return;
            }
            if (nomesCamposPersonalizados.contains(nomeCampo)) {
                mostrarAlerta("Campo Duplicado", "O campo personalizado '" + nomeCampo + "' foi adicionado mais de uma vez.", AlertType.WARNING);
                return;
            }
            nomesCamposPersonalizados.add(nomeCampo);
        }

        ConfiguracaoFormularioDTO dto = new ConfiguracaoFormularioDTO();
        dto.setAcaoId(acaoAtual.getId());
        dto.setUsarNome(chkUsarNome.isSelected());
        dto.setUsarEmail(chkUsarEmail.isSelected());
        dto.setUsarCpf(chkUsarCpf.isSelected());
        dto.setUsarCurso(chkUsarCurso.isSelected());
        dto.setCamposPersonalizados(nomesCamposPersonalizados);

        if (configController.criarConfiguracaoFormulario(dto) != null) {
            this.foiSalvo = true; // SINALIZA O SUCESSO
            fecharJanela();
        } else {
            mostrarAlerta("Erro", "Não foi possível salvar a configuração deste formulário.", AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelar() {
        this.foiSalvo = false; // SINALIZA O CANCELAMENTO
        fecharJanela();
    }

    public boolean foiSalvoComSucesso() {
        return this.foiSalvo;
    }

    private void fecharJanela() {
        if (lblTituloAcao != null && lblTituloAcao.getScene() != null) {
            ((Stage) lblTituloAcao.getScene().getWindow()).close();
        }
    }
    
    private void mostrarAlerta(String titulo, String conteudo, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}