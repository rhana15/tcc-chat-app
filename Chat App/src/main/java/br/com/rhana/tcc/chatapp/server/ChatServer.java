package br.com.rhana.tcc.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    public static final int PORT = 5000;

    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void addClient(ClientHandler client) {

        clients.add(client);
    }

    public static void removeClient(ClientHandler client) {

        clients.remove(client);
    }

    public static CopyOnWriteArrayList<ClientHandler> getClients() {

        return clients;
    }

    public static void broadcast(String message, ClientHandler sender) {

        for (ClientHandler client : clients) {

            if (client != sender) {

                client.sendMessage(message);
            }
        }
    }

    public static void broadcastUserList() {

        StringBuilder sb = new StringBuilder("[USER_LIST]");

        for (ClientHandler client : clients) {

            if (client.getUsername() != null) {

                sb.append(client.getUsername()).append(",");
            }
        }

        for (ClientHandler client : clients) {

            client.sendMessage(sb.toString());
        }
    }

    public static void main(String[] args) {

        System.out.println("Servidor iniciado na porta " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {

                Socket socket = serverSocket.accept();

                ClientHandler client = new ClientHandler(socket);

                new Thread(client).start();

            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
