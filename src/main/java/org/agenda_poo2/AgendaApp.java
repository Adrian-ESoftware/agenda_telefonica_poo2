package org.agenda_poo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principal que inicia a aplicação JavaFX da Agenda de Contatos.
 * Responsável por carregar a interface FXML e configurar a janela principal.
 *
 * @author Adrian
 * @version 1.0
 */
public class AgendaApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(AgendaApp.class.getName());

    /**
     * Inicia a aplicação JavaFX.
     *
     * @param stage o palco principal da aplicação
     * @throws IOException se houver erro ao carregar o arquivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AgendaApp.class.getResource("contatos-view.fxml"));

            if (fxmlLoader.getLocation() == null) {
                LOGGER.severe("Arquivo FXML não encontrado: contatos-view.fxml");
                throw new IOException("Arquivo de interface não encontrado");
            }

            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            stage.setTitle("Agenda de Contatos - Sistema de Gerenciamento");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setOnCloseRequest(event -> {
                LOGGER.info("Aplicação encerrada pelo usuário");
                System.exit(0);
            });
            stage.show();
            LOGGER.info("Aplicação iniciada com sucesso");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar interface FXML", e);
            throw new RuntimeException("Erro ao carregar a interface da aplicação: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao iniciar aplicação", e);
            throw new RuntimeException("Erro inesperado ao iniciar a aplicação: " + e.getMessage(), e);
        }
    }

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        try {
            LOGGER.info("Iniciando Agenda de Contatos...");
            launch(args);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao executar a aplicação", e);
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
            System.exit(1);
        }
    }
}
