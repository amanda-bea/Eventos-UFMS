package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Organizador;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SolicitarEventoFXMLController {

    @FXML private TextField nomeField;
    @FXML private DatePicker dataInicioField;
    @FXML private DatePicker dataFimField;
    @FXML private TextArea descricaoArea;
    @FXML private TextField imagemField;
    @FXML private TextField linkField;
    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private ComboBox<Departamento> departamentoComboBox;

    @FXML private VBox acoesContainerVBox;
    @FXML private Button btnAdicionarAcao;
    @FXML private Button btnSolicitarEventoFinal;

    private EventoController eventoController = new EventoController();
    private Organizador organizadorLogado;

    private List<AcaoFormControls> listaControlesAcoes = new ArrayList<>();
    private int contadorAcao = 0;
    

    private String caminhoImagemSalva;

    @FXML
    private Label nomeArquivoLabel;


    @FXML
    public void initialize() {
        if (categoriaComboBox != null) {
            categoriaComboBox.getItems().setAll(Categoria.values());
        }
        if (departamentoComboBox != null) {
            departamentoComboBox.getItems().setAll(Departamento.values());
        }
        if (btnSolicitarEventoFinal != null) {
            btnSolicitarEventoFinal.setVisible(false);
            btnSolicitarEventoFinal.setManaged(false);
        }
    }
    /**
     * Chamado pelo botão "Escolher Arquivo...".
     * Abre uma janela para o usuário selecionar uma imagem, copia para a pasta
     * do projeto e armazena o caminho relativo.
     */
    @FXML
    private void handleEscolherImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolher Imagem do Evento");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Imagens (*.png, *.jpg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);

        File arquivoSelecionado = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (arquivoSelecionado != null) {
            try {
                // Gera um nome de arquivo único para evitar colisões
                String extensao = getFileExtension(arquivoSelecionado.getName());
                String nomeUnico = UUID.randomUUID().toString() + "." + extensao;
                
                // Define o caminho de destino dentro da pasta do projeto
                Path caminhoDestino = Paths.get("imagens_eventos/" + nomeUnico);
                
                // Cria a pasta se ela não existir
                Files.createDirectories(caminhoDestino.getParent());
                
                // Copia o arquivo selecionado para o destino
                Files.copy(arquivoSelecionado.toPath(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);

                // Guarda o caminho RELATIVO para ser salvo no banco
                this.caminhoImagemSalva = caminhoDestino.toString();
                
                // Dá um feedback visual para o usuário
                nomeArquivoLabel.setText(arquivoSelecionado.getName());
                
                System.out.println("Imagem salva em: " + this.caminhoImagemSalva);

            } catch (IOException e) {
                System.err.println("Erro ao salvar a imagem.");
                e.printStackTrace();
                // Mostrar um alerta de erro
            }
        }
    }

    // Pequeno método auxiliar para pegar a extensão do arquivo
    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }

//Função para voltar à tela de Meus Eventos 
 @FXML
private void voltar(javafx.scene.input.MouseEvent event) {
    try {
        // Obtém o stage atual
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        // Carrega o novo conteúdo
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/MeusEventos.fxml"));
        Parent root = loader.load();

        // Troca o conteúdo da janela atual
        stage.setScene(new Scene(root));
        stage.setTitle("Meus Eventos");
        stage.show();
    } catch (IOException e) {
        System.err.println("Erro ao abrir MeusEventos.fxml: " + e.getMessage());
        e.printStackTrace();
        mostrarAlerta("Erro", "Falha ao abrir Meus Eventos", "Não foi possível abrir a tela Meus Eventos.", Alert.AlertType.ERROR);
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

        if (btnSolicitarEventoFinal != null && !listaControlesAcoes.isEmpty()) {
            btnSolicitarEventoFinal.setText("Concluir e Solicitar Evento");
            btnSolicitarEventoFinal.setVisible(true);
            btnSolicitarEventoFinal.setManaged(true);
        }
    }

    private void removerAcaoForm(VBox containerAcao) {
        Alert confirmacao = new Alert(AlertType.CONFIRMATION, "Tem certeza que deseja remover esta ação?", ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar Remoção");
        confirmacao.setHeaderText("Remover Ação");
        confirmacao.initOwner(getStage());
        Optional<ButtonType> resultado = confirmacao.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            acoesContainerVBox.getChildren().remove(containerAcao);
            listaControlesAcoes.removeIf(controles -> controles.getContainer() == containerAcao);

            if (btnSolicitarEventoFinal != null && listaControlesAcoes.isEmpty()) {
                btnSolicitarEventoFinal.setVisible(false);
                btnSolicitarEventoFinal.setManaged(false);
            }
        }
    }
    
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
        controles.modalidadeAcaoComboBox.setItems(FXCollections.observableArrayList("Presencial", "Online"));
        controles.modalidadeAcaoComboBox.setPromptText("Selecione a Modalidade");
        containerAcao.getChildren().addAll(new Label("Modalidade da Ação*"), controles.modalidadeAcaoComboBox);
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
        try {
            // --- 1. BUSCA O USUÁRIO DA SESSÃO ---
            Usuario criadorDoEvento = SessaoUsuario.getInstancia().getUsuarioLogado();
            if (criadorDoEvento == null) {
                mostrarAlerta("Erro de Autenticação", "Sessão Inválida", 
                            "Não foi possível identificar o usuário logado. Por favor, faça o login novamente.", AlertType.ERROR);
                return;
            }

            // --- 2. VALIDAÇÃO BÁSICA DO EVENTO ---
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

            if (listaControlesAcoes.isEmpty()) {
                mostrarAlerta("Erro de Validação", "Nenhuma Ação Adicionada",
                            "É necessário adicionar pelo menos uma ação ao evento antes de solicitar.", AlertType.ERROR);
                return;
            }

            // --- 3. CRIAÇÃO DO DTO DO EVENTO ---
            EventoDTO eventoDTO = new EventoDTO();
            eventoDTO.setNome(nomeField.getText().trim());
            eventoDTO.setDataInicio(dataInicioEv.toString());
            eventoDTO.setDataFim(dataFimEv != null ? dataFimEv.toString() : null);
            eventoDTO.setCategoria(categoriaComboBox.getValue().name());
            eventoDTO.setDescricao(descricaoArea.getText() != null ? descricaoArea.getText().trim() : "");
            eventoDTO.setDepartamento(departamentoComboBox.getValue().name());
            eventoDTO.setImagem(this.caminhoImagemSalva);
            eventoDTO.setLink(linkField.getText() != null ? linkField.getText().trim() : null);

            // --- 4. COLETA E VALIDAÇÃO DAS AÇÕES ---
            List<AcaoDTO> listaAcoesDTO = new ArrayList<>();
            for (int i = 0; i < listaControlesAcoes.size(); i++) {
                AcaoFormControls controles = listaControlesAcoes.get(i);
                
                // Validação dos campos obrigatórios da ação
                String nomeAcao = controles.nomeAcaoField.getText();
                LocalDate dataAcao = controles.dataAcaoPicker.getValue();
                String localAcao = controles.localAcaoField.getText();
                String horarioInicioStr = controles.horarioInicioAcaoField.getText();
                String horarioFimStr = controles.horarioFimAcaoField.getText();
                Departamento deptoAcao = controles.departamentoAcaoComboBox.getValue();
                String contatoAcao = controles.contatoAcaoField.getText();
                String modalidadeAcao = controles.modalidadeAcaoComboBox.getValue();

                if (nomeAcao == null || nomeAcao.trim().isEmpty() || dataAcao == null ||
                    localAcao == null || localAcao.trim().isEmpty() || 
                    horarioInicioStr == null || horarioInicioStr.trim().isEmpty() ||
                    horarioFimStr == null || horarioFimStr.trim().isEmpty() || 
                    deptoAcao == null ||
                    contatoAcao == null || contatoAcao.trim().isEmpty() || 
                    modalidadeAcao == null || modalidadeAcao.isEmpty()) {
                    mostrarAlerta("Erro de Validação na Ação #" + (i+1), "Campos Obrigatórios da Ação Inválidos",
                                "Preencha todos os campos marcados com '*' para a Ação #" + (i+1) + ".", AlertType.ERROR);
                    return;
                }
                
                // Validação e conversão dos horários
                AcaoDTO acaoDTO = new AcaoDTO();
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
                
                // Configuração do DTO da ação
                acaoDTO.setEvento(eventoDTO.getNome());
                acaoDTO.setNome(nomeAcao.trim());
                acaoDTO.setData(dataAcao.toString());
                acaoDTO.setDescricao(controles.descricaoAcaoArea.getText() != null ? controles.descricaoAcaoArea.getText().trim() : "");
                acaoDTO.setLocal(localAcao.trim());
                acaoDTO.setDepartamento(deptoAcao.name());
                acaoDTO.setContato(contatoAcao.trim());
                acaoDTO.setModalidade(modalidadeAcao);
                acaoDTO.setLink(controles.linkAcaoField.getText() != null ? controles.linkAcaoField.getText().trim() : null);
                
                String capacidadeStr = controles.capacidadeAcaoField.getText();
                acaoDTO.setCapacidade(capacidadeStr != null && !capacidadeStr.trim().isEmpty() ? capacidadeStr.trim() : "0");
                
                listaAcoesDTO.add(acaoDTO);
            }

    // --- 5. DEFINIÇÃO DO FORMULÁRIO ---
    final AtomicBoolean callbackResultadoDefinicaoForm = new AtomicBoolean(false);
    DefinirFormularioFXMLController definirFormController = null;

    try {
        // Carrega a interface de definição de formulário
        java.net.URL fxmlUrl = getClass().getResource("/com/ufms/eventos/view/definirFormulario.fxml");
        if (fxmlUrl == null) {
            System.err.println("ERRO CRÍTICO: Arquivo FXML não encontrado em /com/ufms/eventos/view/definirFormulario.fxml");
            mostrarAlerta("Erro Crítico de UI", "Arquivo FXML Não Encontrado",
                        "Não foi possível encontrar o arquivo de interface 'definirFormulario.fxml'.", AlertType.ERROR);
            return;
        }
        
        // Configura e exibe a tela de definição de formulário
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        definirFormController = loader.getController();
        definirFormController.setEventoParaDefinicao(eventoDTO, callbackResultadoDefinicaoForm::set);

        Stage stageDefinicaoForm = new Stage();
        stageDefinicaoForm.setTitle("Definir Formulário para Evento: " + eventoDTO.getNome());
        stageDefinicaoForm.setScene(new Scene(root));
        stageDefinicaoForm.initModality(Modality.APPLICATION_MODAL);
        stageDefinicaoForm.initOwner(((Node) event.getSource()).getScene().getWindow());
        stageDefinicaoForm.setOnCloseRequest(definirFormController::processarFechamentoJanela);
        stageDefinicaoForm.showAndWait();

    } catch (IOException e) {
        System.err.println("IOException ao carregar definirFormulario.fxml: " + e.getMessage());
        e.printStackTrace();
        mostrarAlerta("Erro de UI", "Falha ao Carregar Interface",
                    "Não foi possível carregar a interface para definir o formulário.", AlertType.ERROR);
        return;
    }

        // --- 6. SALVAMENTO DO EVENTO E AÇÕES ---
            if (callbackResultadoDefinicaoForm.get() && definirFormController != null) {
                // Primeiro salve o evento e as ações
                boolean sucessoCriacaoEventoEAcoes = eventoController.solicitarEventoComAcoes(
                    eventoDTO, listaAcoesDTO, criadorDoEvento);

                if (sucessoCriacaoEventoEAcoes) {
                    // Agora que o evento e ações foram salvos, podemos salvar a configuração do formulário
                    boolean sucessoSalvarFormulario = definirFormController.salvarConfiguracaoNoEvento();
                    
                    if (sucessoSalvarFormulario) {
                        // Sucesso completo - formulário e evento/ações salvos
                        mostrarAlerta("Solicitação Enviada!", "Evento Criado com Sucesso",
                                "Sua solicitação para o evento '" + eventoDTO.getNome() + "' foi enviada e está aguardando aprovação pelo administrador.", 
                                AlertType.INFORMATION);
                        limparFormularioCompleto();
                    } else {
                        // Evento/ações salvos, mas formulário falhou
                        mostrarAlerta("Atenção", "Evento Criado com Configuração Parcial",
                                "O evento foi criado, mas houve um problema ao salvar a configuração do formulário.", 
                                AlertType.WARNING);
                        limparFormularioCompleto();
                    }
                } else {
                    // Falha no salvamento do evento/ações
                    mostrarAlerta("Erro ao Salvar Evento", "Falha na Solicitação",
                            "Ocorreu um erro ao salvar os dados do evento '" + eventoDTO.getNome() + "' ou suas ações.", 
                            AlertType.ERROR);
                }
            } else {
                // Usuário cancelou a definição do formulário ou houve erro
                mostrarAlerta("Operação Cancelada", "Definição de Formulário Cancelada",
                        "A definição do formulário para o evento foi cancelada. Nenhum dado foi salvo.", 
                        AlertType.INFORMATION);
            }
        } catch (Exception e) {
            // Tratamento de exceções inesperadas
            System.err.println("Exceção inesperada em handleSolicitarEventoFinal: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro Inesperado", "Falha ao Processar Solicitação",
                        "Um erro inesperado ocorreu: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void limparFormularioCompleto() {
        nomeField.clear();
        dataInicioField.setValue(null);
        dataFimField.setValue(null);
        descricaoArea.clear();
        linkField.clear();
        if (categoriaComboBox != null) categoriaComboBox.getSelectionModel().clearSelection();
        if (departamentoComboBox != null) departamentoComboBox.getSelectionModel().clearSelection();

        acoesContainerVBox.getChildren().clear();
        listaControlesAcoes.clear();
        contadorAcao = 0;

        if (btnSolicitarEventoFinal != null) {
            btnSolicitarEventoFinal.setVisible(false);
            btnSolicitarEventoFinal.setManaged(false);
        }
    }
    
    private Stage getStage() {
         if (nomeField != null && nomeField.getScene() != null && nomeField.getScene().getWindow() != null) {
            return (Stage) nomeField.getScene().getWindow();
        }
        // System.err.println("Não foi possível obter o Stage a partir do nomeField em getStage()."); // Removido para não poluir console em casos normais
        return null; 
    }

    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        
        Stage currentStage = getStage();
        if (currentStage != null) {
            alert.initOwner(currentStage);
        }
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
        TextField linkAcaoField;
        TextField capacidadeAcaoField;
        
        public VBox getContainer() { return container; }
        public void setContainer(VBox container) { this.container = container; }
    }

    // Método para receber o organizador da tela anterior
    public void initData(Organizador organizador) {
        this.organizadorLogado = organizador;
        System.out.println("Tela de solicitação aberta para o organizador: " + this.organizadorLogado.getNome());
    }
}