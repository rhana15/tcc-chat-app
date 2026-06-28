module br.com.rhana.tcc.chatapp {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.kordamp.bootstrapfx.core;

    opens br.com.rhana.tcc.chatapp.client to javafx.fxml;
    opens br.com.rhana.tcc.chatapp.server to javafx.fxml;

    exports br.com.rhana.tcc.chatapp.client;
    exports br.com.rhana.tcc.chatapp.server;
}