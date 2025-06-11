package com.ufms.eventos.ui;

import com.ufms.eventos.controller.ConfiguracaoFormularioController;
import com.ufms.eventos.controller.RespostaFormularioController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.dto.RespostaFormularioDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FormularioInscricaoFXMLController implements Initializable {

    @FXML private Label tituloAcaoLabel;
    @FXML private VBox formContainer;
    
    private AcaoDTO acaoAtual;
    private RespostaFormularioController respostaController;

    // Mapa para guardar os TextFields criados e conseguir ler seus valores depois
    private Map<String, TextField> camposDoFormulario = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.respostaController = new RespostaFormularioController();
    }

    public void initData(AcaoDTO acao) {
        this.acaoAtual = acao;
        tituloAcaoLabel.setText("Inscrição para: " + acao.getNome());
        
        ConfiguracaoFormularioDTO config = new ConfiguracaoFormularioController()
            .buscarConfiguracaoPorAcaoId(acao.getId())
            .orElse(new ConfiguracaoFormularioDTO(acao.getId(), true, true, false, false, new ArrayList<>()));
        construirFormulario(config);
    }
    
    
    /**
     * Cria e adiciona os campos (Label e TextField) na tela dinamicamente.
     */
    private void construirFormulario(ConfiguracaoFormularioDTO config) {
        formContainer.getChildren().clear();

        if (config.isUsarNome()) adicionarCampo("Nome Completo", "nome", true);
        if (config.isUsarEmail()) adicionarCampo("E-mail", "email", true);
        if (config.isUsarCpf()) adicionarCampo("CPF", "cpf", false);
        if (config.isUsarCurso()) adicionarCampo("Curso/Instituição", "curso", false);

        if (config.getCamposPersonalizados() != null) {
            for (String nomeCampo : config.getCamposPersonalizados()) {
                adicionarCampo(nomeCampo, nomeCampo, false);
            }
        }
    }
    
    private void adicionarCampo(String label, String chave, boolean obrigatorio) {
        Label campoLabel = new Label(label + (obrigatorio ? "*" : ""));
        TextField campoTextField = new TextField();
        campoTextField.setPromptText("Digite seu " + label.toLowerCase());
        
        formContainer.getChildren().addAll(campoLabel, campoTextField);
        camposDoFormulario.put(chave, campoTextField); // Guarda a referência do TextField no mapa
    }

    /**
     * Coleta os dados de todos os campos, valida e envia para a camada de serviço.
     */
    @FXML
    private void handleSubmeter() {
        if (camposDoFormulario.containsKey("nome") && camposDoFormulario.get("nome").getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campo Obrigatório", "Por favor, preencha seu nome completo.");
            return;
        }
        if (camposDoFormulario.containsKey("email") && camposDoFormulario.get("email").getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campo Obrigatório", "Por favor, preencha seu e-mail.");
            return;
        }
        
        RespostaFormularioDTO resposta = new RespostaFormularioDTO();
        
        resposta.setAcaoId(this.acaoAtual.getId());
        // Coleta dados dos campos padrão, se existirem
        if (camposDoFormulario.containsKey("nome")) resposta.setNome(camposDoFormulario.get("nome").getText());
        if (camposDoFormulario.containsKey("email")) resposta.setEmail(camposDoFormulario.get("email").getText());
        if (camposDoFormulario.containsKey("cpf")) resposta.setCpf(camposDoFormulario.get("cpf").getText());
        if (camposDoFormulario.containsKey("curso")) resposta.setCurso(camposDoFormulario.get("curso").getText());
        
        // Coleta dados dos campos extras
        Map<String, String> extras = new HashMap<>();
        camposDoFormulario.forEach((chave, textField) -> {
            if (!chave.equals("nome") && !chave.equals("email") && !chave.equals("cpf") && !chave.equals("curso")) {
                extras.put(chave, textField.getText());
            }
        });

        resposta.setRespostasExtras(extras);
        respostaController.enviarResposta(resposta);
        mostrarAlerta(AlertType.INFORMATION, "Inscrição Realizada", "Sua inscrição na ação '" + acaoAtual.getNome() + "' foi confirmada com sucesso!");
        fecharJanela();
    }

    private void fecharJanela() {
        ((Stage) formContainer.getScene().getWindow()).close();
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String conteudo) {
    Alert alert = new Alert(tipo);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(conteudo);

    // Usa o label do título para encontrar a janela "dona" do alerta.
    if (tituloAcaoLabel != null && tituloAcaoLabel.getScene() != null) {
        alert.initOwner(tituloAcaoLabel.getScene().getWindow());
    }

    alert.showAndWait();
}
}