package com.ufms.eventos.ui;

import com.ufms.eventos.controller.RespostaFormularioController;
import com.ufms.eventos.dto.RespostaFormularioDTO;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.Map;

public class FormularioRespostaFXMLController {

    @FXML private TextField nomeField;
    @FXML private TextField emailField;
    @FXML private TextField cpfField;
    @FXML private TextField cursoField;
    @FXML private VBox camposExtrasBox; // para adicionar dinamicamente os campos

    private String acaoNome; // recebido ao abrir a tela
    private RespostaFormularioController controller = new RespostaFormularioController();

    public void setAcaoNome(String nome) {
        this.acaoNome = nome;
    }

    @FXML
    private void handleEnviar() {
        RespostaFormularioDTO dto = new RespostaFormularioDTO();
        dto.setNome(nomeField.getText());
        dto.setEmail(emailField.getText());
        dto.setCpf(cpfField.getText());
        dto.setCurso(cursoField.getText());
        dto.setAcaoNome(acaoNome);

        Map<String, String> extras = new HashMap<>();
        for (javafx.scene.Node node : camposExtrasBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                Label label = (Label) hbox.getChildren().get(0);
                TextField input = (TextField) hbox.getChildren().get(1);
                extras.put(label.getText(), input.getText());
            }
        }

        dto.setRespostasExtras(extras);
        controller.enviarResposta(dto);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Resposta enviada com sucesso!");
        alert.showAndWait();
    }

    // Esse método pode ser chamado para montar os campos extras dinamicamente
    public void adicionarCampoExtra(String nomeCampo) {
        Label label = new Label(nomeCampo + ":");
        TextField field = new TextField();
        HBox hbox = new HBox(10, label, field);
        camposExtrasBox.getChildren().add(hbox);
    }
}
