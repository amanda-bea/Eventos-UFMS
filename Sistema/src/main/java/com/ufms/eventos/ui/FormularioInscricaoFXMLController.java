package com.ufms.eventos.ui;

import com.ufms.eventos.controller.ConfiguracaoFormularioController;
import com.ufms.eventos.controller.RespostaFormularioController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.dto.RespostaFormularioDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormularioInscricaoFXMLController {

    @FXML private Label tituloAcaoLabel;
    @FXML private VBox formContainer;
    
    private AcaoDTO acaoAtual;
    private ConfiguracaoFormularioController configController;
    private RespostaFormularioController respostaController;

    // Mapa para guardar os TextFields criados e conseguir ler seus valores depois
    private Map<String, TextField> camposDoFormulario = new HashMap<>();

    public void initialize() {
        this.configController = new ConfiguracaoFormularioController();
        this.respostaController = new RespostaFormularioController();
    }

    public void initData(AcaoDTO acao) {
        this.acaoAtual = acao;
        tituloAcaoLabel.setText("Inscrição para: " + acao.getNome());
        
        // Busca a configuração do formulário para esta ação
        ConfiguracaoFormularioDTO config = configController.buscarConfiguracao(acao.getNome());
        
        // Se não houver configuração, cria uma padrão (ou mostra erro)
        if (config == null) {
            config = new ConfiguracaoFormularioDTO(acao.getNome(), true, true, false, false, new ArrayList<>());
        }
        
        // Constrói a UI do formulário dinamicamente
        construirFormulario(config);
    }
    
    private void construirFormulario(ConfiguracaoFormularioDTO config) {
        if (config.isUsarNome()) adicionarCampo("Nome Completo", "nome", true);
        if (config.isUsarEmail()) adicionarCampo("E-mail", "email", true);
        if (config.isUsarCpf()) adicionarCampo("CPF", "cpf", false);
        if (config.isUsarCurso()) adicionarCampo("Curso/Instituição", "curso", false);

        for (String nomeCampo : config.getCamposPersonalizados()) {
            adicionarCampo(nomeCampo, nomeCampo, false);
        }
    }
    
    private void adicionarCampo(String label, String chave, boolean obrigatorio) {
        Label campoLabel = new Label(label + (obrigatorio ? "*" : ""));
        TextField campoTextField = new TextField();
        campoTextField.setPromptText("Digite seu " + label.toLowerCase());
        
        formContainer.getChildren().addAll(campoLabel, campoTextField);
        camposDoFormulario.put(chave, campoTextField); // Guarda a referência
    }

    @FXML
    private void handleSubmeter() {
        RespostaFormularioDTO resposta = new RespostaFormularioDTO();
        resposta.setAcaoNome(acaoAtual.getNome());

        // Coleta dados dos campos padrão
        if (camposDoFormulario.containsKey("nome")) resposta.setNome(camposDoFormulario.get("nome").getText());
        if (camposDoFormulario.containsKey("email")) resposta.setEmail(camposDoFormulario.get("email").getText());
        if (camposDoFormulario.containsKey("cpf")) resposta.setCpf(camposDoFormulario.get("cpf").getText());
        if (camposDoFormulario.containsKey("curso")) resposta.setCurso(camposDoFormulario.get("curso").getText());
        
        // Validação simples
        if (resposta.getNome() == null || resposta.getNome().trim().isEmpty() || 
            resposta.getEmail() == null || resposta.getEmail().trim().isEmpty()) {
            mostrarAlerta("Erro", "Campos obrigatórios não preenchidos.");
            return;
        }

        // Coleta dados dos campos extras
        Map<String, String> extras = new HashMap<>();
        camposDoFormulario.forEach((chave, textField) -> {
            if (!chave.equals("nome") && !chave.equals("email") && !chave.equals("cpf") && !chave.equals("curso")) {
                extras.put(chave, textField.getText());
            }
        });
        resposta.setRespostasExtras(extras);

        // Envia para a camada de serviço
        respostaController.enviarResposta(resposta);

        mostrarAlerta("Sucesso", "Inscrição realizada com sucesso!");
        fecharJanela();
    }

    private void fecharJanela() {
        ((Stage) formContainer.getScene().getWindow()).close();
    }
    
    private void mostrarAlerta(String titulo, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}