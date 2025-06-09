package com.ufms.eventos.ui;

import com.ufms.eventos.controller.ConfiguracaoFormularioController;
import com.ufms.eventos.controller.EventoController;
import com.ufms.eventos.dto.AcaoDTO;
import com.ufms.eventos.dto.EventoDTO;
import com.ufms.eventos.model.Categoria;
import com.ufms.eventos.model.Departamento;
import com.ufms.eventos.model.Usuario;
import com.ufms.eventos.util.SessaoUsuario;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
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
        this.eventoController = new EventoController();
        categoriaComboBox.setItems(FXCollections.observableArrayList(Categoria.values()));
        departamentoComboBox.setItems(FXCollections.observableArrayList(Departamento.values()));
        btnSolicitarEventoFinal.setVisible(false);
        btnSolicitarEventoFinal.setManaged(false);
    }

    //Função para voltar à tela de Meus Eventos Add commentMore actions
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
            mostrarAlerta("Erro", "Falha ao abrir Meus Eventos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSolicitarEventoFinal(ActionEvent event) {
        try {
            Usuario criadorDoEvento = SessaoUsuario.getInstancia().getUsuarioLogado();
            if (criadorDoEvento == null) {
                throw new ValidationException("Sessão inválida. Faça o login novamente.");
            }

            EventoDTO eventoDTO = criarEventoDTO();
            List<AcaoDTO> listaAcoesDTO = coletarAcoesDTO(eventoDTO.getNome());

            boolean formularioFoiSalvo = abrirJanelaParaDefinirFormulario(eventoDTO, event);

            if (formularioFoiSalvo) {
                boolean sucessoCriacao = eventoController.solicitarEventoComAcoes(eventoDTO, listaAcoesDTO, criadorDoEvento);
                if (sucessoCriacao) {
                    mostrarAlerta("Sucesso!", "Sua solicitação de evento foi enviada para análise.", AlertType.INFORMATION);
                    limparFormularioCompleto();
                } else {
                    mostrarAlerta("Erro Crítico", "Ocorreu um erro ao salvar o evento no banco de dados. A configuração do formulário será revertida.", AlertType.ERROR);
                    new ConfiguracaoFormularioController().deletarConfiguracaoFormulario(eventoDTO.getNome());
                }
            } else {
                mostrarAlerta("Operação Cancelada", "A criação do evento foi cancelada.", AlertType.WARNING);
            }

        } catch (ValidationException ve) {
            mostrarAlerta("Erro de Validação", ve.getMessage(), AlertType.WARNING);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro Inesperado", "Ocorreu uma falha geral no sistema: " + e.getMessage(), AlertType.ERROR);
        }
    }

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

    @FXML
    private void handleAdicionarAcao() {
        contadorAcao++;
        AcaoFormControls novosControles = criarFormularioAcaoUI(contadorAcao);
        listaControlesAcoes.add(novosControles);
        acoesContainerVBox.getChildren().add(novosControles.getContainer());
        btnSolicitarEventoFinal.setVisible(true);
        btnSolicitarEventoFinal.setManaged(true);
    }
    
    private void removerAcaoForm(VBox containerAcao, AcaoFormControls controlesParaRemover) {
        acoesContainerVBox.getChildren().remove(containerAcao);
        listaControlesAcoes.remove(controlesParaRemover);
        if (listaControlesAcoes.isEmpty()) {
            btnSolicitarEventoFinal.setVisible(false);
            btnSolicitarEventoFinal.setManaged(false);
        }
    }
    
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

    @FXML
    private void handleEscolherImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolher Imagem do Evento");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens (*.png, *.jpg)", "*.png", "*.jpg", "*.jpeg"));
        File arquivoSelecionado = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (arquivoSelecionado != null) {
            try {
                String extensao = getFileExtension(arquivoSelecionado.getName());
                String nomeUnico = UUID.randomUUID().toString() + "." + extensao;
                Path caminhoDestino = Paths.get("imagens_eventos/" + nomeUnico);
                Files.createDirectories(caminhoDestino.getParent());
                Files.copy(arquivoSelecionado.toPath(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);
                this.caminhoImagemSalva = caminhoDestino.toString().replace("\\", "/");
                nomeArquivoLabel.setText(arquivoSelecionado.getName());
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Falha ao salvar a imagem selecionada.", AlertType.ERROR);
            }
        }
    }

    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse("");
    }

    private boolean abrirJanelaParaDefinirFormulario(EventoDTO eventoDTO, ActionEvent event) throws IOException {
        final AtomicBoolean resultado = new AtomicBoolean(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ufms/eventos/view/DefinirFormulario.fxml"));
        Parent root = loader.load();
        DefinirFormularioFXMLController controller = loader.getController();
        controller.setEventoParaDefinicao(eventoDTO, resultado::set);
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Definir Formulário de Inscrição");
        stage.setScene(new Scene(root));
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.showAndWait();
        
        return resultado.get();
    }

    private void limparFormularioCompleto() {
        nomeField.clear();
        dataInicioField.setValue(null);
        dataFimField.setValue(null);
        descricaoArea.clear();
        nomeArquivoLabel.setText("Nenhum arquivo selecionado");
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

    private void mostrarAlerta(String titulo, String cabecalho, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(cabecalho);
        alert.showAndWait();
    }

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

        public AcaoDTO toDTO(String nomeEventoPai) throws ValidationException {
            String nomeAcao = nomeAcaoField.getText();
            LocalDate dataAcao = dataAcaoPicker.getValue();
            String localAcao = localAcaoField.getText();
            String horarioInicioStr = horarioInicioAcaoField.getText();
            String horarioFimStr = horarioFimAcaoField.getText();
            Departamento deptoAcao = departamentoAcaoComboBox.getValue();
            String contatoAcao = contatoAcaoField.getText();
            String modalidadeAcao = modalidadeAcaoComboBox.getValue();

            if (nomeAcao.trim().isEmpty() || dataAcao == null || localAcao.trim().isEmpty() || horarioInicioStr.trim().isEmpty() ||
                horarioFimStr.trim().isEmpty() || deptoAcao == null || contatoAcao.trim().isEmpty() || modalidadeAcao == null) {
                throw new ValidationException("Preencha todos os campos obrigatórios (*) da Ação: '" + (nomeAcao.isEmpty() ? "Ação sem nome" : nomeAcao) + "'");
            }
            
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
            dto.setCapacidade(capacidadeAcaoField.getText().trim().isEmpty() ? "0" : capacidadeAcaoField.getText().trim());
            return dto;
        }
    }
    
    private static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }
}