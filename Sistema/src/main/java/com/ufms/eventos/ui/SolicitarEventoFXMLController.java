package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class SolicitarEventoFXMLController {

    @FXML private TextField nomeField;
    @FXML private DatePicker dataInicioField;
    @FXML private DatePicker dataFimField;
    @FXML private TextArea descricaoArea;
    @FXML private TextField imagemField;
    @FXML private TextField linkField;
    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private ComboBox<Departamento> departamentoComboBox;
    @FXML private Button solicitarButton;
    private EventoController eventoController = new EventoController();
    private LoginFXMLController loginFXMLController;

    public void setLoginFXMLController(LoginFXMLController loginFXMLController) {
        this.loginFXMLController = loginFXMLController;
    }

    @FXML
    public void initialize() {
        if (categoriaComboBox != null) {
            categoriaComboBox.getItems().setAll(Categoria.values());
        }
        if (departamentoComboBox != null) {
            departamentoComboBox.getItems().setAll(Departamento.values());
        }
    }

    @FXML
    private void handleSolicitarEvento() {
        // Validação dos campos antes de criar o DTO
        if (nomeField.getText() == null || nomeField.getText().trim().isEmpty() ||
            dataInicioField.getValue() == null ||
            categoriaComboBox.getValue() == null ||
            departamentoComboBox.getValue() == null) {
            
            mostrarAlerta("Erro de Validação", "Campos obrigatórios não preenchidos.", 
                          "Por favor, preencha Nome do Evento, Data de Início, Categoria e Departamento.", AlertType.ERROR);
            return;
        }
        
        LocalDate dataInicio = dataInicioField.getValue();
        LocalDate dataFim = dataFimField.getValue();

        if (dataFim != null && dataInicio != null && dataFim.isBefore(dataInicio)) {
            mostrarAlerta("Erro de Validação", "Datas inválidas.", 
                          "A data de fim não pode ser anterior à data de início.", AlertType.ERROR);
            return;
        }

        if (loginFXMLController == null || loginFXMLController.getUsuarioLogado() == null) {
            mostrarAlerta("Erro de Autenticação", "Usuário não logado.",
                          "Você precisa estar logado para solicitar um evento.", AlertType.ERROR);
            return;
        }


        EventoDTO dto = new EventoDTO();
        dto.setNome(nomeField.getText());
        dto.setDataInicio(dataInicio.toString());
        dto.setDataFim(dataFim != null ? dataFim.toString() : "");
        dto.setCategoria(categoriaComboBox.getValue().name());
        dto.setDescricao(descricaoArea.getText());
        dto.setDepartamento(departamentoComboBox.getValue().name());
        dto.setImagem(imagemField.getText());
        dto.setLink(linkField.getText());

        if (eventoController.solicitarEvento(dto, loginFXMLController)) {
            mostrarAlerta("Sucesso", "Solicitação Enviada!", 
                          "Sua solicitação de evento foi enviada com sucesso.", AlertType.INFORMATION);
            limparFormulario();
        } else {
            mostrarAlerta("Erro na Solicitação", "Falha ao Enviar.", 
                          "Ocorreu um erro ao processar sua solicitação de evento.", AlertType.ERROR);
        }
    }

    private void limparFormulario() {
        nomeField.clear();
        dataInicioField.setValue(null);
        dataFimField.setValue(null);
        descricaoArea.clear();
        imagemField.clear();
        linkField.clear();

        if (categoriaComboBox != null) {
            categoriaComboBox.setValue(null);
        }
        if (departamentoComboBox != null) {
            departamentoComboBox.setValue(null);
        }
    }

    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}