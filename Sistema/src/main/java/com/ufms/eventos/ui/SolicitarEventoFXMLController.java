package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Organizador;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SolicitarEventoFXMLController {
    @FXML private TextField nomeField;
    @FXML private TextField dataInicioField;
    @FXML private TextField dataFimField;
    @FXML private TextField categoriaField;
    @FXML private TextArea descricaoArea;
    @FXML private TextField departamentoField;
    @FXML private TextField imagemField;
    @FXML private TextField linkField;

    private EventoController eventoController = new EventoController();
    private LoginFXMLController loginFXMLController;

    public void setLoginFXMLController(LoginFXMLController loginFXMLController) {
        this.loginFXMLController = loginFXMLController;
    }

    @FXML 
    private void handleSolicitarEvento() {
        EventoDTO dto = new EventoDTO();

        dto.setNome(nomeField.getText());
        dto.setDataInicio(dataInicioField.getText());
        dto.setDataFim(dataFimField.getText());
        dto.setCategoria(categoriaField.getText());
        dto.setDescricao(descricaoArea.getText());
        dto.setDepartamento(departamentoField.getText());
        dto.setImagem(imagemField.getText());
        dto.setLink(linkField.getText());

        Organizador organizador = null;
        if (loginFXMLController != null && loginFXMLController.getUsuarioLogado() instanceof Organizador) {
            organizador = (Organizador) loginFXMLController.getUsuarioLogado();
        }

        if (organizador == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro de autenticação");
            alert.setHeaderText(null);
            alert.setContentText("Usuário não autorizado.");
            alert.showAndWait();
            return;
        }

        if (eventoController.solicitarEvento(dto, loginFXMLController)) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Ação solicitada com sucesso!");
            alert.showAndWait();
            limparFormulario();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao solicitar ação.");
            alert.showAndWait();
        }
    }

    private void limparFormulario() {
        nomeField.clear();
        dataInicioField.clear();
        dataFimField.clear();
        categoriaField.clear();
        descricaoArea.clear();
        departamentoField.clear();
        imagemField.clear();
        linkField.clear();
    }



}
