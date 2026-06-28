package br.com.rhana.tcc.chatapp.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {

        this.socket = socket;

        try {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {

        try {

            username = in.readLine();

            ChatServer.addClient(this);

            ChatServer.broadcast(username + " entrou no chat.", this);

            System.out.println(username + " conectado.");

            ChatServer.broadcastUserList();

            String message;

            while ((message = in.readLine()) != null) {

                if (message.startsWith("/private")) {

                    String[] parts = message.split(" ", 3);

                    if (parts.length < 3) {

                        sendMessage("Uso: /private nome mensagem");

                        continue;
                    }

                    String targetName = parts[1];

                    String privateMsg = parts[2];

                    boolean found = false;

                    for (ClientHandler client : ChatServer.getClients()) {

                        if (client.getUsername().equalsIgnoreCase(targetName)) {

                            client.sendMessage("[PRIVADO] " + username + ": " + privateMsg);

                            found = true;

                            break;
                        }
                    }

                    if (!found) {

                        sendMessage("Usuário não encontrado.");
                    }

                    continue;
                }

                ChatServer.broadcast(username + ": " + message, this);
            }

        } catch (IOException e) {

            System.out.println(username + " desconectou.");

        } finally {

            try {

                ChatServer.removeClient(this);

                ChatServer.broadcast(username + " saiu do chat.", this);

                ChatServer.broadcastUserList();

                socket.close();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}