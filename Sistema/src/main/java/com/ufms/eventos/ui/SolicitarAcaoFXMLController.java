package com.ufms.eventos.ui;

import com.ufms.eventos.controller.AcaoController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Organizador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class SolicitarAcaoFXMLController {

    @FXML private TextField eventoField;
    @FXML private TextField nomeField;
    @FXML private TextField dataField;
    @FXML private TextArea descricaoArea;
    @FXML private TextField localField;
    @FXML private TextField horarioInicioField;
    @FXML private TextField horarioFimField;
    @FXML private TextField departamentoField;
    @FXML private TextField contatoField;
    @FXML private TextField modalidadeField;
    @FXML private TextField imagemField;
    @FXML private TextField linkField;
    @FXML private TextField statusField;
    @FXML private TextField capacidadeField;

    private AcaoController acaoController = new AcaoController();
    private LoginFXMLController loginFXMLController; 
    private EventoDTO eventoDTO;                      // evento atual

    public void setLoginFXMLController(LoginFXMLController loginFXMLController) {
        this.loginFXMLController = loginFXMLController;
    }

    public void setEventoDTO(EventoDTO eventoDTO) {
        this.eventoDTO = eventoDTO;
    }

    @FXML
    private void handleSolicitarAcao() {
        AcaoDTO dto = new AcaoDTO();

        // Preencher os campos do DTO com os dados do formulário
        dto.setEvento(eventoField.getText());
        dto.setNome(nomeField.getText());
        dto.setData(dataField.getText());
        dto.setDescricao(descricaoArea.getText());
        dto.setLocal(localField.getText());
        dto.setHorarioInicio(horarioInicioField.getText());
        dto.setHorarioFim(horarioFimField.getText());
        dto.setDepartamento(departamentoField.getText());
        dto.setContato(contatoField.getText());
        dto.setModalidade(modalidadeField.getText());
        dto.setImagem(imagemField.getText());
        dto.setLink(linkField.getText());
        dto.setCapacidade(capacidadeField.getText());

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

        boolean sucesso = acaoController.solicitarAcao(dto, loginFXMLController, eventoDTO);

        if (sucesso) {
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
        eventoField.clear();
        nomeField.clear();
        dataField.clear();
        descricaoArea.clear();
        localField.clear();
        horarioInicioField.clear();
        horarioFimField.clear();
        departamentoField.clear();
        contatoField.clear();
        modalidadeField.clear();
        imagemField.clear();
        linkField.clear();
        statusField.clear();
        capacidadeField.clear();
    }
}
