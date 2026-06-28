package br.com.rhana.tcc.chatapp.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.util.Optional;

public class ChatController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextFlow chatAreaFlow;

    @FXML
    private TextField messageField;

    @FXML
    private ListView<String> usersListView;

    private ChatConnection connection;

    private String username;

    @FXML
    public void initialize() {

        Platform.runLater(() -> {

            askUsername();

            try {

                connection = new ChatConnection();

                connection.connect("localhost", 5000);

                connection.send(username);

                startReceiverThread();

            } catch (Exception e) {

                appendMessage(new ChatMessage("Sistema", "Erro ao conectar ao servidor. Certifique-se de que o Server está ligado!"));
            }
        });

        usersListView.setOnMouseClicked(event -> {

            if (event.getClickCount() == 2) {

                String selectedUser = usersListView.getSelectionModel().getSelectedItem();

                if (selectedUser != null && !selectedUser.equals(username)) {

                    messageField.setText("/private " + selectedUser + " ");

                    messageField.requestFocus();

                    messageField.positionCaret(messageField.getText().length());
                }
            }
        });

        chatAreaFlow.heightProperty().addListener((observable, oldValue, newValue) -> {

            scrollPane.setVvalue(1.0);
        });
    }

    private void askUsername() {

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Login");

        dialog.setHeaderText("Digite seu nome para entrar no chat:");

        while (true) {

            Optional<String> result = dialog.showAndWait();

            if (!result.isPresent()) {

                Platform.exit();

                System.exit(0);
            }

            username = result.get().trim();

            if (!username.isEmpty()) {

                break;
            }

            dialog.setHeaderText("AVISO: O nome não pode ficar em branco!\nDigite seu nome para entrar:");
        }
    }

    private void startReceiverThread() {

        Thread thread = new Thread(() -> {

            try {

                String message;

                while ((message = connection.receive()) != null) {

                    if (message.startsWith("[USER_LIST]")) {

                        String dados = message.replace("[USER_LIST]", "");

                        String[] usuarios = dados.split(",");

                        Platform.runLater(() -> {

                            usersListView.getItems().clear();

                            for (String user : usuarios) {

                                if (!user.isBlank()) {

                                    usersListView.getItems().add(user);
                                }
                            }
                        });

                    } else {

                        if (message.contains(": ")) {

                            String[] partes = message.split(": ", 2);
                            String remetenteReal = partes[0];
                            String conteudoReal = partes[1];

                            if (remetenteReal.startsWith("[PRIVADO]")) {

                                appendMessage(new ChatMessage("[PRIVADO]", remetenteReal.replace("[PRIVADO]", "").trim() + ": " + conteudoReal));

                            } else {

                                appendMessage(new ChatMessage(remetenteReal, conteudoReal));
                            }
                        } else {

                            appendMessage(new ChatMessage("Sistema", message));
                        }
                    }

                }

            } catch (IOException e) {

                appendMessage(new ChatMessage("Sistema", "Conexão encerrada."));

            } finally {

                Platform.runLater(() -> {

                    Platform.exit();

                    System.exit(0);
                });
            }
        });

        thread.setDaemon(true);

        thread.start();
    }

    @FXML
    private void sendMessage() {
        String text = messageField.getText();

        if (text.isBlank()) {
            return;
        }

        connection.send(text);

        ChatMessage msgPropria = new ChatMessage("Eu", text);
        appendMessage(msgPropria);

        messageField.clear();
    }

    private void appendMessage(ChatMessage msg) {
        Platform.runLater(() -> {

            String textoExibicao;

            if (msg.getSender().equals("Sistema") || msg.getSender().equals("[PRIVADO]")) {

                textoExibicao = msg.getContent() + "\n";

            } else {

                textoExibicao = msg.getSender() + ": " + msg.getContent() + "\n";
            }

            Text textNode = new Text(textoExibicao);
            textNode.setFont(Font.font("System", 14));

            if (msg.getSender().equals("[PRIVADO]")) {

                textNode.setFill(Color.DARKRED);
                textNode.setFont(Font.font("System", FontPosture.ITALIC, 14));
            }
            else if (msg.getSender().equals("Eu")) {

                textNode.setFill(Color.DARKBLUE);
            }
            else {
                textNode.setFill(Color.BLACK);
            }

            chatAreaFlow.getChildren().add(textNode);
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }
}