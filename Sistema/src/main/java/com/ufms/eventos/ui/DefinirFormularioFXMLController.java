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

/**
 * Controlador para a tela de definição de formulários personalizados para eventos.
 * Permite configurar quais campos serão exigidos dos participantes.
 */
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
    private ConfiguracaoFormularioDTO configuracaoTemporaria;
    
    private Consumer<Boolean> onCloseCallback;
    private boolean configuracaoSalvaComSucesso = false;

    /**
     * Inicializa o controlador após o carregamento do FXML.
     */
    public void initialize() {
        this.configuracaoLogicController = new ConfiguracaoFormularioController();
        // Valores padrão para novos formulários
        chkUsarNome.setSelected(true);
        chkUsarEmail.setSelected(true);
    }

    /**
     * Define o evento para o qual o formulário será configurado e o callback para quando a tela for fechada.
     * @param evento O evento para configuração do formulário
     * @param callback O callback que será chamado quando a tela fechar (recebe true se o formulário foi salvo)
     */
    public void setEventoParaDefinicao(EventoDTO evento, Consumer<Boolean> callback) {
        this.eventoAtual = evento;
        this.onCloseCallback = callback;
        
        System.out.println("Definindo formulário para evento: " + (evento != null ? evento.getNome() : "nulo"));

        if (eventoAtual != null && eventoAtual.getNome() != null && !eventoAtual.getNome().trim().isEmpty()) {
            lblTituloEvento.setText("Definir Formulário para Evento: " + eventoAtual.getNome());
            
            // Verificamos se já existe uma configuração (apenas para eventos existentes no banco)
            try {
                carregarConfiguracaoExistente(eventoAtual.getNome());
            } catch (Exception e) {
                System.out.println("Evento ainda não está no banco, usando configuração padrão: " + e.getMessage());
                // Se o evento ainda não existe no banco, usamos a configuração padrão
                // Isso é normal para novos eventos
            }
        } else {
            lblTituloEvento.setText("Definir Formulário para Evento (Erro: Evento não especificado)");
            mostrarAlerta("Erro Crítico", "Evento não especificado", 
                         "Não é possível definir o formulário sem um evento associado.", AlertType.ERROR);
                         
            if (getStage() != null) { // Garante que a UI está pronta
                 ((Button) getStage().getScene().lookup("#btnFinalizarFormulario")).setDisable(true);
                 ((Button) getStage().getScene().lookup("#btnAdicionarCampoPersonalizado")).setDisable(true);
            }
        }
    }

    /**
     * Tenta carregar uma configuração de formulário existente para o evento.
     * @param identificadorEvento O identificador do evento (nome)
     */
    private void carregarConfiguracaoExistente(String identificadorEvento) {
        System.out.println("Tentando carregar configuração para: " + identificadorEvento);
        ConfiguracaoFormularioDTO configExistente = null;
        
        try {
            configExistente = configuracaoLogicController.buscarConfiguracao(identificadorEvento);
        } catch (Exception e) {
            System.out.println("Erro ao buscar configuração: " + e.getMessage());
            return;
        }
        
        if (configExistente != null) {
            System.out.println("Configuração encontrada para: " + identificadorEvento);
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
        } else {
            System.out.println("Nenhuma configuração encontrada para: " + identificadorEvento);
        }
    }

    /**
     * Manipula o evento de adicionar um novo campo personalizado.
     */
    @FXML
    private void handleAdicionarCampoPersonalizado(ActionEvent event) {
        adicionarLinhaCampoPersonalizadoUI(null);
    }

    /**
     * Adiciona uma nova linha de campo personalizado à interface.
     * @param nomeInicialCampo O nome inicial do campo (null para campo vazio)
     */
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

    /**
     * Manipula o evento de finalização do formulário.
     */
    @FXML
    private void handleFinalizarFormulario(ActionEvent event) {
        if (eventoAtual == null || eventoAtual.getNome() == null || eventoAtual.getNome().trim().isEmpty()) {
            mostrarAlerta("Erro", "Evento não definido", 
                         "Não é possível salvar o formulário sem um evento associado.", AlertType.ERROR);
            return;
        }
        String identificadorEvento = eventoAtual.getNome(); // Usando o nome do evento como identificador
        System.out.println("Finalizando formulário para evento: " + identificadorEvento);

        // Coleta e valida os campos personalizados
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

        // Cria o DTO com a configuração do formulário
        configuracaoTemporaria = new ConfiguracaoFormularioDTO(
            identificadorEvento,
            chkUsarNome.isSelected(),
            chkUsarEmail.isSelected(),
            chkUsarCpf.isSelected(),
            chkUsarCurso.isSelected(),
            nomesCamposPersonalizados
        );

        // Não salva agora, apenas marca como bem-sucedido e fecha
        System.out.println("Configuração de formulário preparada para: " + identificadorEvento);
        configuracaoSalvaComSucesso = true;
        fecharJanela();
    }

    /**
     * Manipula o evento de cancelamento da configuração.
     */
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

    /**
     * Processa o evento de fechamento da janela, verificando se há alterações não salvas.
     */
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

    /**
     * Fecha a janela do formulário.
     */
    private void fecharJanela() {
        processarFechamentoJanela(null); // Chama o callback
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }
    
    /**
     * Obtém a referência do Stage atual.
     */
    private Stage getStage() {
         if (lblTituloEvento != null && lblTituloEvento.getScene() != null) {
            return (Stage) lblTituloEvento.getScene().getWindow();
        }
        return null;
    }

    /**
     * Mostra um alerta para o usuário.
     */
    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.initOwner(getStage());
        alert.showAndWait();
    }
    
    /**
     * Retorna a configuração temporária do formulário.
     * Esta configuração deve ser salva depois que o evento for criado no banco.
     */
    public ConfiguracaoFormularioDTO getConfiguracaoTemporaria() {
        return configuracaoTemporaria;
    }
    
    /**
     * Salva a configuração do formulário para um evento já existente no banco.
     * Este método deve ser chamado após a criação do evento.
     */
    public boolean salvarConfiguracaoNoEvento() {
        if (configuracaoTemporaria == null || eventoAtual == null) {
            System.err.println("Erro: configuracaoTemporaria ou eventoAtual é nulo");
            return false;
        }
        
        String nomeEvento = eventoAtual.getNome();
        
        try {
            // Aguarda um pouco para garantir que o evento esteja no banco
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("Tentando salvar configuração para o evento: " + nomeEvento);
                
            ConfiguracaoFormularioDTO resultado = 
                configuracaoLogicController.criarConfiguracaoFormulario(configuracaoTemporaria);
                    
            if (resultado != null) {
                System.out.println("Configuração salva com sucesso para o evento: " + nomeEvento);
                return true;
            } else {
                System.err.println("Falha ao salvar configuração: retorno nulo do controller");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar configuração do formulário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 
