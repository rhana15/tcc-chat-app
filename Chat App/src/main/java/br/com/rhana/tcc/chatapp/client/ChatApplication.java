package br.com.rhana.tcc.chatapp.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ChatApplication extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("/br/com/rhana/tcc/chatapp/client/view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stage.setTitle("Chat App - JavaFX");

        stage.setScene(scene);

        stage.show();
    }
}
