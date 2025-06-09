package com.ufms.eventos.ui;

import com.ufms.eventos.controller.ConfiguracaoFormularioController;
import com.ufms.eventos.dto.ConfiguracaoFormularioDTO;
import com.ufms.eventos.dto.EventoDTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DefinirFormularioFXMLController {

    @FXML private Label lblTituloEvento;
    @FXML private CheckBox chkUsarNome;
    @FXML private CheckBox chkUsarEmail;
    @FXML private CheckBox chkUsarCpf;
    @FXML private CheckBox chkUsarCurso;
    @FXML private VBox camposPersonalizadosContainer;

    private EventoDTO eventoAtual;
    private ConfiguracaoFormularioController configuracaoLogicController;
    private List<HBox> camposPersonalizadosRows = new ArrayList<>();

    private Consumer<Boolean> onCloseCallback;
    private boolean configuracaoSalvaComSucesso = false;

    public void initialize() {
        this.configuracaoLogicController = new ConfiguracaoFormularioController();
        chkUsarNome.setSelected(true);
        chkUsarEmail.setSelected(true);
    }


    public void setEventoParaDefinicao(EventoDTO evento, Consumer<Boolean> callback) {
        this.eventoAtual = evento;
        this.onCloseCallback = callback;

        if (eventoAtual != null && eventoAtual.getNome() != null && !eventoAtual.getNome().trim().isEmpty()) {
            lblTituloEvento.setText("Definir Formulário para Evento: " + eventoAtual.getNome());
            // O identificador do evento será o nome do evento.
            // Se você tiver um ID numérico, use eventoAtual.getId() ou similar.
            carregarConfiguracaoExistente(eventoAtual.getNome());
        } else {
            lblTituloEvento.setText("Definir Formulário para Evento (Erro: Evento não especificado)");
            mostrarAlerta("Erro Crítico", "Evento não especificado.", "Não é possível definir o formulário sem um evento associado.", AlertType.ERROR);
            if (getStage() != null) { // Garante que a UI está pronta
                 ((Button) getStage().getScene().lookup("#btnFinalizarFormulario")).setDisable(true);
                 ((Button) getStage().getScene().lookup("#btnAdicionarCampoPersonalizado")).setDisable(true);
            }
        }
    }

    private void carregarConfiguracaoExistente(String identificadorEvento) {
        ConfiguracaoFormularioDTO configExistente = configuracaoLogicController.buscarConfiguracao(identificadorEvento);
        if (configExistente != null) {
            chkUsarNome.setSelected(configExistente.isUsarNome());
            chkUsarEmail.setSelected(configExistente.isUsarEmail());
            chkUsarCpf.setSelected(configExistente.isUsarCpf());
            chkUsarCurso.setSelected(configExistente.isUsarCurso());

            camposPersonalizadosContainer.getChildren().clear();
            camposPersonalizadosRows.clear();
            if (configExistente.getCamposPersonalizados() != null) {
                for (String nomeCampo : configExistente.getCamposPersonalizados()) {
                    adicionarLinhaCampoPersonalizadoUI(nomeCampo);
                }
            }
        }
    }

    @FXML
    private void handleAdicionarCampoPersonalizado(ActionEvent event) {
        adicionarLinhaCampoPersonalizadoUI(null);
    }

    private void adicionarLinhaCampoPersonalizadoUI(String nomeInicialCampo) {
        HBox linhaCampo = new HBox(10);
        linhaCampo.setAlignment(Pos.CENTER_LEFT);
        linhaCampo.setPadding(new Insets(0,0,0,5));

        TextField nomeCampoField = new TextField(nomeInicialCampo);
        nomeCampoField.setPromptText("Nome do Campo Personalizado");
        HBox.setHgrow(nomeCampoField, Priority.ALWAYS);

        Button btnRemoverCampo = new Button("Remover");
        btnRemoverCampo.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
        btnRemoverCampo.setOnAction(e -> {
            camposPersonalizadosContainer.getChildren().remove(linhaCampo);
            camposPersonalizadosRows.remove(linhaCampo);
        });

        Label bullet = new Label("•");
        bullet.setPadding(new Insets(0,5,0,0));
        linhaCampo.getChildren().addAll(bullet, nomeCampoField, btnRemoverCampo);
        camposPersonalizadosContainer.getChildren().add(linhaCampo);
        camposPersonalizadosRows.add(linhaCampo);
    }

    @FXML
    private void handleFinalizarFormulario(ActionEvent event) {
        if (eventoAtual == null || eventoAtual.getNome() == null || eventoAtual.getNome().trim().isEmpty()) {
            mostrarAlerta("Erro", "Evento não definido.", "Não é possível salvar o formulário sem um evento associado.", AlertType.ERROR);
            return;
        }
        String identificadorEvento = eventoAtual.getNome(); // Usando o nome do evento como identificador

        List<String> nomesCamposPersonalizados = new ArrayList<>();
        for (HBox row : camposPersonalizadosRows) {
            Node node = row.getChildren().get(1);
            if (node instanceof TextField) {
                String nomeCampo = ((TextField) node).getText();
                if (nomeCampo != null && !nomeCampo.trim().isEmpty()) {
                    if (nomesCamposPersonalizados.contains(nomeCampo.trim())) {
                        mostrarAlerta("Campo Duplicado", "Nome de Campo Personalizado Repetido",
                                      "O campo '" + nomeCampo.trim() + "' foi adicionado mais de uma vez. Por favor, use nomes únicos.", AlertType.WARNING);
                        return;
                    }
                    nomesCamposPersonalizados.add(nomeCampo.trim());
                } else {
                    mostrarAlerta("Campo Inválido", "Nome de Campo Personalizado Vazio",
                                  "Preencha o nome de todos os campos personalizados ou remova os vazios.", AlertType.WARNING);
                    return;
                }
            }
        }

        ConfiguracaoFormularioDTO configDTO = new ConfiguracaoFormularioDTO(
            identificadorEvento,
            chkUsarNome.isSelected(),
            chkUsarEmail.isSelected(),
            chkUsarCpf.isSelected(),
            chkUsarCurso.isSelected(),
            nomesCamposPersonalizados
        );

        ConfiguracaoFormularioDTO resultadoOperacao;


        resultadoOperacao = configuracaoLogicController.criarConfiguracaoFormulario(configDTO);

        if (resultadoOperacao != null) {
            mostrarAlerta("Sucesso!", "Configuração Salva", "A configuração do formulário para o evento '" + eventoAtual.getNome() + "' foi salva.", AlertType.INFORMATION);
            configuracaoSalvaComSucesso = true;
            fecharJanela();
        } else {
            mostrarAlerta("Erro", "Falha ao Salvar", "Não foi possível salvar a configuração do formulário.", AlertType.ERROR);
            configuracaoSalvaComSucesso = false;
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        Alert confirmacao = new Alert(AlertType.CONFIRMATION,
                "Tem certeza que deseja cancelar? As alterações nesta configuração de formulário não serão salvas.",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar Cancelamento");
        confirmacao.setHeaderText("Cancelar Definição de Formulário para o Evento");
        confirmacao.initOwner(getStage());

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            configuracaoSalvaComSucesso = false;
            fecharJanela();
        }
    }

    public void processarFechamentoJanela(WindowEvent event) {
        if (!configuracaoSalvaComSucesso) {
            if (event != null) { // Fechamento pelo botão 'X' da janela
                Alert confirmacao = new Alert(AlertType.CONFIRMATION,
                        "Você tem alterações não salvas. Deseja fechar e descartá-las?",
                        ButtonType.YES, ButtonType.NO);
                confirmacao.setTitle("Confirmar Fechamento");
                confirmacao.setHeaderText("Alterações Não Salvas");
                confirmacao.initOwner(getStage());
                Optional<ButtonType> resultado = confirmacao.showAndWait();

                if (resultado.isPresent() && resultado.get() == ButtonType.NO) {
                    if(event != null) event.consume();
                    return;
                }
            }
        }
        if (onCloseCallback != null) {
            onCloseCallback.accept(configuracaoSalvaComSucesso);
        }
    }

    private void fecharJanela() {
        processarFechamentoJanela(null); // Chama o callback
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }
    
    private Stage getStage() {
         if (lblTituloEvento != null && lblTituloEvento.getScene() != null) {
            return (Stage) lblTituloEvento.getScene().getWindow();
        }
        return null;
    }

    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.initOwner(getStage());
        alert.showAndWait();
    }
}