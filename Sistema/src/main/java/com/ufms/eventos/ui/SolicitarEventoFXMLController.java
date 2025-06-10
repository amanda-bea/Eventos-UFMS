package com.ufms.eventos.ui;

import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Acao;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.ResourceBundle;

public class SolicitarEventoFXMLController implements Initializable {

    // --- CAMPOS FXML ---
    @FXML private TextField nomeField;
    @FXML private DatePicker dataInicioField;
    @FXML private DatePicker dataFimField;
    @FXML private TextArea descricaoArea;
    @FXML private Label nomeArquivoLabel;
    @FXML private TextField linkField;
    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private ComboBox<Departamento> departamentoComboBox;
    @FXML private VBox acoesContainerVBox;
    @FXML private Button btnSolicitarEventoFinal;

    // --- DADOS E CONTROLLERS ---
    private EventoController eventoController;
    private String caminhoImagemSalva;
    private List<AcaoFormControls> listaControlesAcoes = new ArrayList<>();
    private int contadorAcao = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Inicializando SolicitarEventoFXMLController...");
        
        // Inicializar o controlador
        this.eventoController = new EventoController();
        
        // Verificar estado dos componentes cruciais
        System.out.println("nomeArquivoLabel: " + (nomeArquivoLabel != null ? "OK" : "NULO"));
        System.out.println("categoriaComboBox: " + (categoriaComboBox != null ? "OK" : "NULO"));
        System.out.println("departamentoComboBox: " + (departamentoComboBox != null ? "OK" : "NULO"));
        
        // Garantir que nomeArquivoLabel existe e tem um texto padrão
        Platform.runLater(() -> {
            if (nomeArquivoLabel == null) {
                System.err.println("ERRO CRÍTICO: nomeArquivoLabel continua nulo após inicialização");
            } else {
                System.out.println("nomeArquivoLabel inicializado com sucesso");
                nomeArquivoLabel.setText("Nenhum arquivo selecionado");
            }
        });
        
        // Inicializar ComboBoxes se não forem nulos
        if (categoriaComboBox != null) {
            categoriaComboBox.setItems(FXCollections.observableArrayList(Categoria.values()));
        }
        
        if (departamentoComboBox != null) {
            departamentoComboBox.setItems(FXCollections.observableArrayList(Departamento.values()));
        }
        
        // Configurar o contêiner de ações
        if (btnSolicitarEventoFinal != null) {
            btnSolicitarEventoFinal.setVisible(false);
            btnSolicitarEventoFinal.setManaged(false);
        }
    }

    /**
     * Lógica principal e completa para submeter a solicitação do evento.
     */
    @FXML
    private void handleSolicitarEventoFinal(ActionEvent event) {
        EventoDTO eventoDTO = null;
        try {
            Usuario criadorDoEvento = SessaoUsuario.getInstancia().getUsuarioLogado();
            eventoDTO = criarEventoDTO();
            List<AcaoDTO> listaAcoesDTO = coletarAcoesDTO(eventoDTO.getNome());

            // 1. Salva o evento e as ações primeiro
            List<Acao> acoesSalvas = eventoController.solicitarEventoComAcoes(eventoDTO, listaAcoesDTO, criadorDoEvento);
            if (acoesSalvas.isEmpty()) throw new Exception("Falha ao salvar evento/ações.");

            // 2. Loop para configurar o formulário de CADA ação
            boolean operacaoCancelada = false;
            for (Acao acao : acoesSalvas) {
                boolean formConfigurado = abrirJanelaDefinirFormulario(new AcaoDTO(acao), event);
                if (!formConfigurado) {
                    operacaoCancelada = true;
                    break;
                }
            }

            // 3. Avalia o resultado
            if (operacaoCancelada) {
                mostrarAlerta("Operação Cancelada", "A criação do evento foi interrompida e revertida.", AlertType.WARNING);
                eventoController.deletarEventoCompleto(eventoDTO.getNome());
            } else {
                mostrarAlerta("Sucesso!", "Evento e formulários criados com sucesso!", AlertType.INFORMATION);
                limparFormularioCompleto();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (eventoDTO != null) {
                eventoController.deletarEventoCompleto(eventoDTO.getNome());
            }
            mostrarAlerta("Erro", "Ocorreu uma falha: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private boolean abrirJanelaDefinirFormulario(AcaoDTO acao, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/DefinirFormulario.fxml"));
        Parent root = loader.load();
        DefinirFormularioFXMLController controller = loader.getController();
        controller.initData(acao); // Passa a AÇÃO específica
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Definir Formulário para Ação: " + acao.getNome());
        stage.setScene(new Scene(root));
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.showAndWait();
        
        return controller.foiSalvoComSucesso();
    }

    /**
     * Coleta os dados dos campos do evento e cria o DTO.
     */
    private EventoDTO criarEventoDTO() throws ValidationException {
        if (nomeField.getText().trim().isEmpty() || dataInicioField.getValue() == null || categoriaComboBox.getValue() == null || departamentoComboBox.getValue() == null) {
            throw new ValidationException("Preencha todos os campos obrigatórios (*) do Evento.");
        }
        LocalDate dataInicioEv = dataInicioField.getValue();
        LocalDate dataFimEv = dataFimField.getValue();
        if (dataFimEv != null && dataFimEv.isBefore(dataInicioEv)) {
            throw new ValidationException("A data de fim do evento não pode ser anterior à data de início.");
        }

        EventoDTO dto = new EventoDTO();
        dto.setNome(nomeField.getText().trim());
        dto.setDataInicio(dataInicioEv.toString());
        dto.setDataFim(dataFimEv != null ? dataFimEv.toString() : dataInicioEv.toString());
        dto.setCategoria(categoriaComboBox.getValue().name());
        dto.setDepartamento(departamentoComboBox.getValue().name());
        dto.setDescricao(descricaoArea.getText());
        dto.setLink(linkField.getText());
        dto.setImagem(this.caminhoImagemSalva);
        return dto;
    }

    /**
     * Itera sobre os formulários de ação, valida e coleta os dados em DTOs.
     */
    private List<AcaoDTO> coletarAcoesDTO(String nomeEventoPai) throws ValidationException {
        if (listaControlesAcoes.isEmpty()) {
            throw new ValidationException("Adicione pelo menos uma ação ao evento.");
        }
        List<AcaoDTO> lista = new ArrayList<>();
        for (AcaoFormControls controles : listaControlesAcoes) {
            lista.add(controles.toDTO(nomeEventoPai));
        }
        return lista;
    }
    
    /**
     * Adiciona um novo formulário de ação à tela.
     */
    @FXML
    private void handleAdicionarAcao() {
        contadorAcao++;
        AcaoFormControls novosControles = criarFormularioAcaoUI(contadorAcao);
        listaControlesAcoes.add(novosControles);
        acoesContainerVBox.getChildren().add(novosControles.getContainer());
        btnSolicitarEventoFinal.setVisible(true);
        btnSolicitarEventoFinal.setManaged(true);
    }
    
    /**
     * Remove um formulário de ação da tela.
     */
    private void removerAcaoForm(VBox containerAcao, AcaoFormControls controlesParaRemover) {
        acoesContainerVBox.getChildren().remove(containerAcao);
        listaControlesAcoes.remove(controlesParaRemover);
        if (listaControlesAcoes.isEmpty()) {
            btnSolicitarEventoFinal.setVisible(false);
            btnSolicitarEventoFinal.setManaged(false);
        }
    }
    
    /**
     * Cria programaticamente todos os componentes visuais para um formulário de ação.
     */
    private AcaoFormControls criarFormularioAcaoUI(int numeroAcao) {
        VBox containerAcao = new VBox(10);
        containerAcao.setPadding(new Insets(15));
        containerAcao.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
        AcaoFormControls controles = new AcaoFormControls(containerAcao);

        Label tituloAcao = new Label("Dados da Ação #" + numeroAcao);
        tituloAcao.setFont(Font.font("System", FontWeight.BOLD, 14));
        Button btnRemover = new Button("X");
        btnRemover.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-cursor: hand;");
        btnRemover.setOnAction(e -> removerAcaoForm(containerAcao, controles));
        
        HBox cabecalho = new HBox(tituloAcao, new javafx.scene.layout.Region(), btnRemover);
        HBox.setHgrow(cabecalho.getChildren().get(1), Priority.ALWAYS);
        
        controles.nomeAcaoField.setPromptText("Ex: Palestra de Abertura");
        controles.descricaoAcaoArea.setPromptText("Detalhes sobre a ação...");
        controles.localAcaoField.setPromptText("Ex: Anfiteatro 1");
        controles.horarioInicioAcaoField.setPromptText("HH:mm");
        controles.horarioFimAcaoField.setPromptText("HH:mm");
        controles.contatoAcaoField.setPromptText("Telefone ou e-mail de contato");
        controles.linkAcaoField.setPromptText("Link para material ou sala online");
        controles.capacidadeAcaoField.setPromptText("0 para ilimitado");
        
        containerAcao.getChildren().addAll(
            cabecalho,
            new Label("Nome da Ação*"), controles.nomeAcaoField,
            new Label("Descrição"), controles.descricaoAcaoArea,
            new Label("Data da Ação*"), controles.dataAcaoPicker,
            new Label("Local da Ação*"), controles.localAcaoField,
            new HBox(10, 
                new VBox(5, new Label("Horário Início* (HH:mm)"), controles.horarioInicioAcaoField),
                new VBox(5, new Label("Horário Fim* (HH:mm)"), controles.horarioFimAcaoField)),
            new Label("Departamento da Ação*"), controles.departamentoAcaoComboBox,
            new Label("Contato da Ação*"), controles.contatoAcaoField,
            new Label("Modalidade da Ação*"), controles.modalidadeAcaoComboBox,
            new Label("Link da Ação"), controles.linkAcaoField,
            new Label("Capacidade (Nº Vagas)"), controles.capacidadeAcaoField
        );
        return controles;
    }

    /**
     * Abre a janela para o usuário escolher uma imagem local.
     */
    @FXML
private void handleEscolherImagem(ActionEvent event) {
    // Método totalmente reescrito para evitar NullPointerException
    try {
        // Criar e configurar o FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolher Imagem do Evento");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imagens (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));
        
        // Abrir o diálogo de seleção de arquivo
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);
        
        // Se nenhum arquivo foi selecionado, retorne
        if (arquivoSelecionado == null) {
            System.out.println("DEBUG: Nenhum arquivo selecionado pelo usuário");
            return;
        }
        
        // Processar o arquivo selecionado
        String nomeArquivo = arquivoSelecionado.getName();
        String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);
        String nomeUnico = UUID.randomUUID().toString() + "." + extensao;
        
        // Criar o diretório se não existir
        File diretorio = new File("imagens_eventos");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
        
        // Copiar o arquivo para o destino
        Path destino = Paths.get("imagens_eventos", nomeUnico);
        Files.copy(arquivoSelecionado.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        
        // Salvar o caminho da imagem
        this.caminhoImagemSalva = destino.toString().replace("\\", "/");
        
        // Atualizar a UI com o nome do arquivo, com tratamento seguro para evitar NPE
        Platform.runLater(() -> {
            try {
                if (nomeArquivoLabel != null) {
                    nomeArquivoLabel.setText(nomeArquivo);
                } else {
                    System.err.println("ERRO CRÍTICO: nomeArquivoLabel ainda é nulo após Platform.runLater");
                    mostrarAlerta("Aviso", "O arquivo foi salvo mas a interface não pôde ser atualizada.", AlertType.WARNING);
                }
            } catch (Exception e) {
                System.err.println("Exceção ao atualizar nomeArquivoLabel: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta("Aviso", "O arquivo foi salvo mas a interface não pôde ser atualizada.", AlertType.WARNING);
            }
        });
        
    } catch (Exception e) {
        System.err.println("Erro ao processar imagem: " + e.getMessage());
        e.printStackTrace();
        mostrarAlerta("Erro", "Não foi possível processar a imagem selecionada: " + e.getMessage(), AlertType.ERROR);
    }
}
    
    /**
     * Limpa todos os campos do formulário para uma nova solicitação.
     */
    private void limparFormularioCompleto() {
        nomeField.clear();
        dataInicioField.setValue(null);
        dataFimField.setValue(null);
        descricaoArea.clear();
        if (nomeArquivoLabel != null) nomeArquivoLabel.setText("Nenhum arquivo selecionado");
        caminhoImagemSalva = null;
        linkField.clear();
        categoriaComboBox.setValue(null);
        departamentoComboBox.setValue(null);
        acoesContainerVBox.getChildren().clear();
        listaControlesAcoes.clear();
        contadorAcao = 0;
        btnSolicitarEventoFinal.setVisible(false);
        btnSolicitarEventoFinal.setManaged(false);
    }

    /**
     * Exibe um diálogo de alerta simples.
     */
    private void mostrarAlerta(String titulo, String conteudo, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    /**
     * Navega de volta para a tela de Meus Eventos, como você enviou.
     */
    @FXML
    private void voltar(MouseEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/MeusEventos.fxml"));
            Parent root = loader.load();
            stage.getScene().setRoot(root); // Usar setRoot é um pouco melhor que setScene
            stage.setTitle("Meus Eventos");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao voltar para a tela 'Meus Eventos'.", AlertType.ERROR);
        }
    }

    // --- CLASSE INTERNA E EXCEÇÃO ---

    /**
     * Classe interna simples para agrupar todos os controles de um formulário de ação.
     */
    private static class AcaoFormControls {
        private final VBox container;
        final TextField nomeAcaoField = new TextField();
        final DatePicker dataAcaoPicker = new DatePicker();
        final TextArea descricaoAcaoArea = new TextArea();
        final TextField localAcaoField = new TextField();
        final TextField horarioInicioAcaoField = new TextField();
        final TextField horarioFimAcaoField = new TextField();
        final ComboBox<Departamento> departamentoAcaoComboBox = new ComboBox<>(FXCollections.observableArrayList(Departamento.values()));
        final TextField contatoAcaoField = new TextField();
        final ComboBox<String> modalidadeAcaoComboBox = new ComboBox<>(FXCollections.observableArrayList("Presencial", "Online"));
        final TextField linkAcaoField = new TextField();
        final TextField capacidadeAcaoField = new TextField("0");

        public AcaoFormControls(VBox container) { this.container = container; }
        public VBox getContainer() { return container; }

        /**
         * Valida os dados dos campos e os converte para um AcaoDTO.
         * @throws ValidationException se algum campo obrigatório for inválido.
         */
        public AcaoDTO toDTO(String nomeEventoPai) throws ValidationException {
            String nomeAcao = nomeAcaoField.getText();
            if (nomeAcao == null || nomeAcao.trim().isEmpty()) throw new ValidationException("O nome da ação é obrigatório.");
            
            LocalDate dataAcao = dataAcaoPicker.getValue();
            if (dataAcao == null) throw new ValidationException("A data da ação '" + nomeAcao + "' é obrigatória.");
            
            String localAcao = localAcaoField.getText();
            if (localAcao == null || localAcao.trim().isEmpty()) throw new ValidationException("O local da ação '" + nomeAcao + "' é obrigatório.");
            
            String horarioInicioStr = horarioInicioAcaoField.getText();
            if (horarioInicioStr == null || horarioInicioStr.trim().isEmpty()) throw new ValidationException("O horário de início da ação '" + nomeAcao + "' é obrigatório.");
            
            String horarioFimStr = horarioFimAcaoField.getText();
            if (horarioFimStr == null || horarioFimStr.trim().isEmpty()) throw new ValidationException("O horário de fim da ação '" + nomeAcao + "' é obrigatório.");

            Departamento deptoAcao = departamentoAcaoComboBox.getValue();
            if (deptoAcao == null) throw new ValidationException("O departamento da ação '" + nomeAcao + "' é obrigatório.");
            
            String contatoAcao = contatoAcaoField.getText();
            if (contatoAcao == null || contatoAcao.trim().isEmpty()) throw new ValidationException("O contato da ação '" + nomeAcao + "' é obrigatório.");
            
            String modalidadeAcao = modalidadeAcaoComboBox.getValue();
            if (modalidadeAcao == null) throw new ValidationException("A modalidade da ação '" + nomeAcao + "' é obrigatória.");

            AcaoDTO dto = new AcaoDTO();
            try {
                LocalTime inicio = LocalTime.parse(horarioInicioStr);
                LocalTime fim = LocalTime.parse(horarioFimStr);
                if (fim.isBefore(inicio)) {
                    throw new ValidationException("O horário de fim não pode ser anterior ao de início na ação '" + nomeAcao + "'.");
                }
                dto.setHorarioInicio(inicio.toString());
                dto.setHorarioFim(fim.toString());
            } catch (DateTimeParseException e) {
                throw new ValidationException("Formato de horário inválido (use HH:mm) na ação '" + nomeAcao + "'.");
            }
            
            dto.setEvento(nomeEventoPai);
            dto.setNome(nomeAcao.trim());
            dto.setData(dataAcao.toString());
            dto.setDescricao(descricaoAcaoArea.getText());
            dto.setLocal(localAcao.trim());
            dto.setDepartamento(deptoAcao.name());
            dto.setContato(contatoAcao.trim());
            dto.setModalidade(modalidadeAcao);
            dto.setLink(linkAcaoField.getText());
            String capacidade = capacidadeAcaoField.getText().trim();
            dto.setCapacidade(capacidade.isEmpty() ? "0" : capacidade);
            return dto;
        }
    }
    
    /**
     * Exceção customizada para erros de validação do formulário.
     */
    private static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }
}