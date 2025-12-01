package org.agenda_poo2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.model.Contato;
import org.model.ContatoDAO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContatosController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ContatosController.class.getName());

    @FXML
    private TextField tfNome;

    @FXML
    private TextField tfTelefone;

    @FXML
    private TextField tfEmail;

    @FXML
    private Button btnAdicionar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnExcluir;

    @FXML
    private Button btnLimpar;

    @FXML
    private TableView<Contato> tabelaContatos;

    @FXML
    private TableColumn<Contato, Integer> colId;

    @FXML
    private TableColumn<Contato, String> colNome;

    @FXML
    private TableColumn<Contato, String> colTelefone;

    @FXML
    private TableColumn<Contato, String> colEmail;

    @FXML
    private Label lblStatus;

    private Contato contatoSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Configurar colunas da tabela
            colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            colNome.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
            colTelefone.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefone()));
            colEmail.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

            // Listener para selecionar contato na tabela
            tabelaContatos.setOnMouseClicked(event -> selecionarContato());

            // Carregar contatos ao iniciar
            carregarContatos();
            LOGGER.info("Controller inicializado com sucesso");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar controller", e);
            mostrarErro("Erro ao inicializar a aplicação", e.getMessage());
        }
    }

    /**
     * Adiciona um novo contato ao banco de dados.
     */
    @FXML
    public void onAdicionarContato() {
        try {
            if (!validarCampos()) {
                return;
            }

            Contato novoContato = new Contato();
            novoContato.setNome(tfNome.getText().trim());
            novoContato.setTelefone(tfTelefone.getText().trim());
            novoContato.setEmail(tfEmail.getText().trim());

            ContatoDAO.salvarContato(novoContato);
            mostrarSucesso("Contato adicionado com sucesso!");
            limparCampos();
            carregarContatos();
            LOGGER.info("Novo contato adicionado: " + novoContato.getNome());
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validação inválida", e);
            mostrarErro("Erro de validação", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao adicionar contato", e);
            mostrarErro("Erro ao salvar contato",
                "Não foi possível salvar o contato. Verifique a conexão com o banco de dados.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao adicionar contato", e);
            mostrarErro("Erro inesperado", e.getMessage());
        }
    }

    /**
     * Edita um contato existente.
     */
    @FXML
    public void onEditarContato() {
        try {
            if (contatoSelecionado == null) {
                mostrarAviso("Selecione um contato para editar!");
                return;
            }

            if (!validarCampos()) {
                return;
            }

            String nomeAntigo = contatoSelecionado.getNome();
            contatoSelecionado.setNome(tfNome.getText().trim());
            contatoSelecionado.setTelefone(tfTelefone.getText().trim());
            contatoSelecionado.setEmail(tfEmail.getText().trim());

            ContatoDAO.atualizarContato(contatoSelecionado);
            mostrarSucesso("Contato atualizado com sucesso!");
            LOGGER.info("Contato atualizado: " + nomeAntigo + " -> " + contatoSelecionado.getNome());
            limparCampos();
            carregarContatos();
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validação inválida", e);
            mostrarErro("Erro de validação", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar contato", e);
            mostrarErro("Erro ao atualizar",
                "Não foi possível atualizar o contato. Verifique a conexão com o banco de dados.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar contato", e);
            mostrarErro("Erro inesperado", e.getMessage());
        }
    }

    /**
     * Exclui um contato do banco de dados.
     */
    @FXML
    public void onExcluirContato() {
        try {
            if (contatoSelecionado == null) {
                mostrarAviso("Selecione um contato para excluir!");
                return;
            }

            int idContato = contatoSelecionado.getId();
            String nomeContato = contatoSelecionado.getNome();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Exclusão");
            alert.setHeaderText("Deseja realmente excluir este contato?");
            alert.setContentText("Nome: " + nomeContato);

            if (alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent()) {
                ContatoDAO.excluirContato(idContato);
                mostrarSucesso("Contato excluído com sucesso!");
                LOGGER.info("Contato excluído: ID " + idContato + " - " + nomeContato);
                limparCampos();  // Isso já seta contatoSelecionado = null
                carregarContatos();
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validação inválida", e);
            mostrarErro("Erro de validação", e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir contato", e);
            mostrarErro("Erro ao excluir",
                "Não foi possível excluir o contato. Verifique a conexão com o banco de dados.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao excluir contato", e);
            mostrarErro("Erro inesperado", e.getMessage());
        }
    }

    /**
     * Limpa todos os campos de entrada.
     */
    @FXML
    public void onLimpar() {
        limparCampos();
        LOGGER.info("Campos limpos");
    }

    /**
     * Seleciona um contato da tabela e preenche os campos.
     */
    private void selecionarContato() {
        try {
            contatoSelecionado = tabelaContatos.getSelectionModel().getSelectedItem();
            if (contatoSelecionado != null) {
                tfNome.setText(contatoSelecionado.getNome());
                tfTelefone.setText(contatoSelecionado.getTelefone());
                tfEmail.setText(contatoSelecionado.getEmail());
                lblStatus.setText("Contato selecionado: " + contatoSelecionado.getNome());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao selecionar contato", e);
            mostrarErro("Erro", "Não foi possível selecionar o contato");
        }
    }

    /**
     * Carrega todos os contatos do banco de dados e exibe na tabela.
     */
    private void carregarContatos() {
        try {
            List<Contato> contatos = ContatoDAO.listarContatos();
            ObservableList<Contato> observableContatos = FXCollections.observableArrayList(contatos);
            tabelaContatos.setItems(observableContatos);
            lblStatus.setText("Total de contatos: " + contatos.size());
            LOGGER.info("Contatos carregados com sucesso. Total: " + contatos.size());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar contatos", e);
            lblStatus.setText("Erro ao carregar contatos");
            mostrarErro("Erro ao carregar",
                "Não foi possível carregar os contatos do banco de dados.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao carregar contatos", e);
            lblStatus.setText("Erro ao carregar contatos");
            mostrarErro("Erro inesperado", e.getMessage());
        }
    }

    /**
     * Limpa todos os campos de entrada.
     */
    private void limparCampos() {
        tfNome.clear();
        tfTelefone.clear();
        tfEmail.clear();
        contatoSelecionado = null;
        tabelaContatos.getSelectionModel().clearSelection();
        lblStatus.setText("Pronto");
    }

    /**
     * Valida se os campos obrigatórios estão preenchidos.
     *
     * @return true se os campos são válidos, false caso contrário
     */
    private boolean validarCampos() {
        String nome = tfNome.getText().trim();
        String telefone = tfTelefone.getText().trim();
        String email = tfEmail.getText().trim();

        if (nome.isEmpty()) {
            mostrarAviso("Por favor, preencha o campo Nome!");
            tfNome.requestFocus();
            return false;
        }

        if (telefone.isEmpty()) {
            mostrarAviso("Por favor, preencha o campo Telefone!");
            tfTelefone.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            mostrarAviso("Por favor, preencha o campo Email!");
            tfEmail.requestFocus();
            return false;
        }

        if (!validarEmail(email)) {
            mostrarAviso("Email inválido! Use o formato: exemplo@dominio.com");
            tfEmail.requestFocus();
            return false;
        }

        if (!validarTelefone(telefone)) {
            mostrarAviso("Telefone inválido! Use um formato válido com números e caracteres como ( ) -");
            tfTelefone.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Valida o formato do email.
     *
     * @param email o email a validar
     * @return true se o email é válido, false caso contrário
     */
    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Valida o formato do telefone.
     *
     * @param telefone o telefone a validar
     * @return true se o telefone é válido, false caso contrário
     */
    private boolean validarTelefone(String telefone) {
        return telefone.matches("^[()\\d\\s\\-+]*$") && telefone.length() >= 8;
    }

    /**
     * Exibe mensagem de sucesso ao usuário.
     *
     * @param mensagem a mensagem a exibir
     */
    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Exibe mensagem de aviso ao usuário.
     *
     * @param mensagem a mensagem a exibir
     */
    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Exibe mensagem de erro ao usuário.
     *
     * @param titulo o título da mensagem
     * @param mensagem a mensagem de erro
     */
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
