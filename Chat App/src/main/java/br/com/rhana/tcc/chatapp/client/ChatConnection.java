package br.com.rhana.tcc.chatapp.client;

import java.io.*;
import java.net.Socket;

public class ChatConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connect(String host, int port) throws IOException {

        socket = new Socket(host, port);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String message) {

        if (out != null) {

            out.println(message);
        }
    }

    public String receive() throws IOException {

        return in.readLine();
    }

    public void close() throws IOException {

        if (socket != null) {

            socket.close();
        }
    }
}
