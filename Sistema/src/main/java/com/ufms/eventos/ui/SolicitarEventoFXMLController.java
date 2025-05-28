package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SolicitarEventoFXMLController {

    // Campos do Evento Principal
    @FXML private TextField nomeField;
    @FXML private DatePicker dataInicioField;
    @FXML private DatePicker dataFimField;
    @FXML private TextArea descricaoArea;
    @FXML private TextField imagemField;
    @FXML private TextField linkField;
    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private ComboBox<Departamento> departamentoComboBox;

    // Campos e containers para Ações
    @FXML private VBox acoesContainerVBox;
    @FXML private Button btnAdicionarAcao;
    @FXML private Button btnSolicitarEventoFinal; // Este é o botão que será manipulado

    private EventoController eventoController = new EventoController();
    private LoginFXMLController loginFXMLController;

    private List<AcaoFormControls> listaControlesAcoes = new ArrayList<>();
    private int contadorAcao = 0;

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

        // Garante que o botão de solicitar final esteja INVISÍVEL inicialmente.
        // O FXML já o define como visible="false" managed="false", mas reforçamos aqui.
        if (btnSolicitarEventoFinal != null) {
            btnSolicitarEventoFinal.setVisible(false);
            btnSolicitarEventoFinal.setManaged(false);
        }
    }

    @FXML
    private void handleVoltar(javafx.scene.input.MouseEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAdicionarAcao(ActionEvent event) {
        contadorAcao++;
        AcaoFormControls novosControlesAcao = criarFormularioAcaoUI(contadorAcao);
        listaControlesAcoes.add(novosControlesAcao);
        acoesContainerVBox.getChildren().add(novosControlesAcao.getContainer());

        // Torna o botão "Solicitar Evento Final" visível e gerenciado
        // APÓS adicionar a primeira (ou qualquer) ação.
        if (btnSolicitarEventoFinal != null && !btnSolicitarEventoFinal.isVisible()) {
            btnSolicitarEventoFinal.setText("Concluir e Solicitar Evento"); // Texto padrão
            btnSolicitarEventoFinal.setVisible(true);
            btnSolicitarEventoFinal.setManaged(true);
        }
    }

    private void removerAcaoForm(VBox containerAcao) {
        Alert confirmacao = new Alert(AlertType.CONFIRMATION, "Tem certeza que deseja remover esta ação?", ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover Ação");
        Optional<ButtonType> resultado = confirmacao.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            acoesContainerVBox.getChildren().remove(containerAcao);
            listaControlesAcoes.removeIf(controles -> controles.getContainer() == containerAcao);

            // O botão btnSolicitarEventoFinal permanece visível mesmo se todas as ações forem removidas,
            // pois o usuário já indicou a intenção de trabalhar com o fluxo de ações.
            // Não há necessidade de mudar o texto dele aqui.
        }
    }
    
    // O método criarFormularioAcaoUI permanece o mesmo da resposta anterior
    private AcaoFormControls criarFormularioAcaoUI(int numeroAcao) {
        VBox containerAcao = new VBox(10);
        containerAcao.setPadding(new Insets(15, 10, 15, 10));
        containerAcao.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");

        Label tituloAcao = new Label("Dados da Ação #" + numeroAcao);
        tituloAcao.setFont(Font.font("System", FontWeight.BOLD, 14));
        tituloAcao.setTextFill(javafx.scene.paint.Color.web("#2f4a51"));
        Button btnRemoverAcao = new Button("Remover Ação #" + numeroAcao);
        btnRemoverAcao.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-cursor: hand;");
        btnRemoverAcao.setOnAction(e -> removerAcaoForm(containerAcao));

        HBox tituloBox = new HBox(10, tituloAcao, btnRemoverAcao);
        HBox.setHgrow(tituloAcao, Priority.ALWAYS);
        containerAcao.getChildren().add(tituloBox);

        AcaoFormControls controles = new AcaoFormControls();
        controles.setContainer(containerAcao);

        controles.nomeAcaoField = new TextField();
        controles.nomeAcaoField.setPromptText("Nome da Ação #" + numeroAcao);
        containerAcao.getChildren().addAll(new Label("Nome da Ação*"), controles.nomeAcaoField);

        controles.descricaoAcaoArea = new TextArea();
        controles.descricaoAcaoArea.setPromptText("Descrição da Ação #" + numeroAcao);
        controles.descricaoAcaoArea.setPrefHeight(60);
        containerAcao.getChildren().addAll(new Label("Descrição da Ação"), controles.descricaoAcaoArea);
        
        controles.dataAcaoPicker = new DatePicker();
        controles.dataAcaoPicker.setPromptText("dd/MM/yyyy");
        containerAcao.getChildren().addAll(new Label("Data da Ação*"), controles.dataAcaoPicker);

        controles.localAcaoField = new TextField();
        controles.localAcaoField.setPromptText("Local da Ação #" + numeroAcao);
        containerAcao.getChildren().addAll(new Label("Local da Ação*"), controles.localAcaoField);

        HBox horariosBox = new HBox(10);
        controles.horarioInicioAcaoField = new TextField();
        controles.horarioInicioAcaoField.setPromptText("HH:mm");
        VBox inicioBox = new VBox(5, new Label("Horário Início* (HH:mm)"), controles.horarioInicioAcaoField);
        controles.horarioFimAcaoField = new TextField();
        controles.horarioFimAcaoField.setPromptText("HH:mm");
        VBox fimBox = new VBox(5, new Label("Horário Fim* (HH:mm)"), controles.horarioFimAcaoField);
        HBox.setHgrow(inicioBox, Priority.ALWAYS); HBox.setHgrow(fimBox, Priority.ALWAYS);
        horariosBox.getChildren().addAll(inicioBox, fimBox);
        containerAcao.getChildren().add(horariosBox);

        controles.departamentoAcaoComboBox = new ComboBox<>();
        if (departamentoComboBox != null && departamentoComboBox.getItems() != null) {
             controles.departamentoAcaoComboBox.setItems(FXCollections.observableArrayList(Departamento.values()));
        }
        controles.departamentoAcaoComboBox.setPromptText("Selecione Depto. da Ação");
        containerAcao.getChildren().addAll(new Label("Departamento da Ação*"), controles.departamentoAcaoComboBox);

        controles.contatoAcaoField = new TextField();
        controles.contatoAcaoField.setPromptText("Telefone ou Email para contato");
        containerAcao.getChildren().addAll(new Label("Contato da Ação*"), controles.contatoAcaoField);

        controles.modalidadeAcaoComboBox = new ComboBox<>();
        controles.modalidadeAcaoComboBox.setItems(FXCollections.observableArrayList("Presencial", "Online", "Híbrido"));
        controles.modalidadeAcaoComboBox.setPromptText("Selecione a Modalidade");
        containerAcao.getChildren().addAll(new Label("Modalidade da Ação*"), controles.modalidadeAcaoComboBox);

        controles.imagemAcaoField = new TextField();
        controles.imagemAcaoField.setPromptText("https://exemplo.com/imagem_acao.png");
        containerAcao.getChildren().addAll(new Label("Imagem da Ação (URL)"), controles.imagemAcaoField);

        controles.linkAcaoField = new TextField();
        controles.linkAcaoField.setPromptText("Link adicional para a ação");
        containerAcao.getChildren().addAll(new Label("Link da Ação"), controles.linkAcaoField);

        controles.capacidadeAcaoField = new TextField();
        controles.capacidadeAcaoField.setPromptText("Ex: 50 (0 para ilimitado)");
        controles.capacidadeAcaoField.textProperty().addListener((obs, ov, nv) -> {
            if (!nv.matches("\\d*")) {
                controles.capacidadeAcaoField.setText(nv.replaceAll("[^\\d]", ""));
            }
        });
        containerAcao.getChildren().addAll(new Label("Capacidade (Nº Vagas)"), controles.capacidadeAcaoField);
        
        return controles;
    }


    @FXML
    private void handleSolicitarEventoFinal(ActionEvent event) {
        // 1. Validação dos campos do Evento Principal (mesma lógica anterior)
        if (nomeField.getText() == null || nomeField.getText().trim().isEmpty() ||
            dataInicioField.getValue() == null ||
            categoriaComboBox.getValue() == null ||
            departamentoComboBox.getValue() == null) {
            mostrarAlerta("Erro de Validação", "Campos Obrigatórios do Evento",
                          "Por favor, preencha: Nome do Evento, Data de Início, Categoria e Departamento.", AlertType.ERROR);
            return;
        }
        LocalDate dataInicioEv = dataInicioField.getValue();
        LocalDate dataFimEv = dataFimField.getValue();
        if (dataFimEv != null && dataInicioEv != null && dataFimEv.isBefore(dataInicioEv)) {
            mostrarAlerta("Erro de Validação", "Datas do Evento Inválidas",
                          "A data de fim do evento não pode ser anterior à data de início.", AlertType.ERROR);
            return;
        }
        if (loginFXMLController == null || loginFXMLController.getUsuarioLogado() == null) {
            mostrarAlerta("Erro de Autenticação", "Usuário Não Logado",
                          "Você precisa estar logado para solicitar um evento.", AlertType.ERROR);
            return;
        }

        // 2. Criar EventoDTO (mesma lógica anterior)
        EventoDTO eventoDTO = new EventoDTO();
        eventoDTO.setNome(nomeField.getText().trim());
        eventoDTO.setDataInicio(dataInicioEv.toString());
        eventoDTO.setDataFim(dataFimEv != null ? dataFimEv.toString() : "");
        eventoDTO.setCategoria(categoriaComboBox.getValue().name());
        eventoDTO.setDescricao(descricaoArea.getText().trim());
        eventoDTO.setDepartamento(departamentoComboBox.getValue().name());
        eventoDTO.setImagem(imagemField.getText().trim());
        eventoDTO.setLink(linkField.getText().trim());

        // 3. Coletar e Validar Ações (mesma lógica anterior)
        List<AcaoDTO> listaAcoesDTO = new ArrayList<>();
        for (int i = 0; i < listaControlesAcoes.size(); i++) {
            AcaoFormControls controles = listaControlesAcoes.get(i);
            AcaoDTO acaoDTO = new AcaoDTO();
            
            String nomeAcao = controles.nomeAcaoField.getText();
            LocalDate dataAcao = controles.dataAcaoPicker.getValue();
            String localAcao = controles.localAcaoField.getText();
            String horarioInicioStr = controles.horarioInicioAcaoField.getText();
            String horarioFimStr = controles.horarioFimAcaoField.getText();
            Departamento deptoAcao = controles.departamentoAcaoComboBox.getValue();
            String contatoAcao = controles.contatoAcaoField.getText();
            String modalidadeAcao = controles.modalidadeAcaoComboBox.getValue();

            if (nomeAcao == null || nomeAcao.trim().isEmpty() || dataAcao == null ||
                localAcao == null || localAcao.trim().isEmpty() || horarioInicioStr == null || horarioInicioStr.trim().isEmpty() ||
                horarioFimStr == null || horarioFimStr.trim().isEmpty() || deptoAcao == null ||
                contatoAcao == null || contatoAcao.trim().isEmpty() || modalidadeAcao == null || modalidadeAcao.isEmpty()) {
                mostrarAlerta("Erro de Validação na Ação #" + (i+1), "Campos Obrigatórios da Ação Inválidos",
                              "Preencha todos os campos marcados com '*' para a Ação #" + (i+1) + ".", AlertType.ERROR);
                return;
            }
            
            try {
                LocalTime horarioInicioAcao = LocalTime.parse(horarioInicioStr);
                LocalTime horarioFimAcao = LocalTime.parse(horarioFimStr);
                if (horarioFimAcao.isBefore(horarioInicioAcao)) {
                     mostrarAlerta("Erro de Validação na Ação #" + (i+1), "Horários da Ação Inválidos",
                                  "O horário de fim não pode ser anterior ao de início para a Ação #" + (i+1) + ".", AlertType.ERROR);
                    return;
                }
                acaoDTO.setHorarioInicio(horarioInicioAcao.toString());
                acaoDTO.setHorarioFim(horarioFimAcao.toString());
            } catch (DateTimeParseException e) {
                 mostrarAlerta("Erro de Validação na Ação #" + (i+1), "Formato de Horário Inválido",
                                  "Use o formato HH:mm para os horários da Ação #" + (i+1) + ".", AlertType.ERROR);
                return;
            }
            
            acaoDTO.setEvento(eventoDTO.getNome());
            acaoDTO.setNome(nomeAcao.trim());
            acaoDTO.setData(dataAcao.toString());
            acaoDTO.setDescricao(controles.descricaoAcaoArea.getText() != null ? controles.descricaoAcaoArea.getText().trim() : "");
            acaoDTO.setLocal(localAcao.trim());
            acaoDTO.setDepartamento(deptoAcao.name());
            acaoDTO.setContato(contatoAcao.trim());
            acaoDTO.setModalidade(modalidadeAcao);
            acaoDTO.setImagem(controles.imagemAcaoField.getText() != null ? controles.imagemAcaoField.getText().trim() : "");
            acaoDTO.setLink(controles.linkAcaoField.getText() != null ? controles.linkAcaoField.getText().trim() : "");
            String capacidadeStr = controles.capacidadeAcaoField.getText();
            acaoDTO.setCapacidade(capacidadeStr != null && !capacidadeStr.trim().isEmpty() ? capacidadeStr.trim() : "0");
            
            listaAcoesDTO.add(acaoDTO);
        }
        
        // 4. Chamar o serviço (mesma lógica anterior)
        System.out.println("Evento DTO a ser enviado: " + eventoDTO.toString());
        System.out.println("Ações DTO a serem enviadas: " + listaAcoesDTO.size());
        listaAcoesDTO.forEach(acao -> System.out.println("  " + acao.toString()));

        // Chamar o método real do EventoController que lida com EventoDTO e List<AcaoDTO>
        // boolean sucesso = eventoController.solicitarEventoComAcoes(eventoDTO, listaAcoesDTO, loginFXMLController);
        // if (sucesso) {
        //     mostrarAlerta("Sucesso!", "Solicitação de Evento e Ações Enviada", "Sua solicitação foi enviada com sucesso.", AlertType.INFORMATION);
        //     limparFormularioCompleto();
        // } else {
        //     mostrarAlerta("Erro", "Falha ao Enviar Solicitação", "Ocorreu um erro ao processar sua solicitação.", AlertType.ERROR);
        // }
        mostrarAlerta("Submissão (Simulada)", "Dados Coletados", "Verifique o console para os dados do evento e " + listaAcoesDTO.size() + " ação(ões). \nImplemente a chamada final ao serviço.", AlertType.INFORMATION);
        limparFormularioCompleto();
    }

    private void limparFormularioCompleto() {
        // Limpa campos do evento principal
        nomeField.clear();
        dataInicioField.setValue(null);
        dataFimField.setValue(null);
        descricaoArea.clear();
        imagemField.clear();
        linkField.clear();
        if (categoriaComboBox != null) categoriaComboBox.getSelectionModel().clearSelection();
        if (departamentoComboBox != null) departamentoComboBox.getSelectionModel().clearSelection();

        // Limpa ações dinâmicas
        acoesContainerVBox.getChildren().clear();
        listaControlesAcoes.clear();
        contadorAcao = 0;

        // Reconfigura botão de solicitar final para o estado inicial (oculto)
        if (btnSolicitarEventoFinal != null) {
            btnSolicitarEventoFinal.setVisible(false);
            btnSolicitarEventoFinal.setManaged(false);
            // O texto não precisa ser resetado para "sem ações" pois ele só aparece
            // com o texto "Concluir e Solicitar Evento"
        }
    }

    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    private static class AcaoFormControls {
        private VBox container;
        TextField nomeAcaoField;
        DatePicker dataAcaoPicker;
        TextArea descricaoAcaoArea;
        TextField localAcaoField;
        TextField horarioInicioAcaoField;
        TextField horarioFimAcaoField;
        ComboBox<Departamento> departamentoAcaoComboBox;
        TextField contatoAcaoField;
        ComboBox<String> modalidadeAcaoComboBox;
        TextField imagemAcaoField;
        TextField linkAcaoField;
        TextField capacidadeAcaoField;
        
        public VBox getContainer() { return container; }
        public void setContainer(VBox container) { this.container = container; }
    }
}