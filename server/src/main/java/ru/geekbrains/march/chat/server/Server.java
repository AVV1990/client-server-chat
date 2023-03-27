package ru.geekbrains.march.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>(); // когда сервер запускается, список клиентов пустой.

        // подключаем клиентов
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Сервер запущен на порту " + port);

//            чтобы могло подключиться несколько клиентов, мы берем в бесконечный цикл
            while (true) {
                System.out.println("Ждем нового клиента....");

                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket); //  добавляем клиентов в список рассылки  на СЕБЯ
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //  когда добавляем клиента, мы его включаем в список, чтобы получать сообщения от сервера
    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    // отключаем от рассылки, чтобы не получать сообщения от сервера
    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    // broadcastMassage - метод, который позволяет разослать всем клиентам сообщение

    public void broadcastMassage(String message) throws IOException {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public boolean isNickBusy(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                return true;
            }
        }
        return false; // если прошлись по всем клиентам, и ни у кого не нашли такой ник, то говорим, что не занят
    }

    public void sendPrivateMsg(String toNickName, String msg, String fromNickName) throws IOException {
        for (ClientHandler client: clients) {
            if (client.getUsername().equals(toNickName)){
                client.sendMessage(fromNickName + " ---> "+ toNickName + " " + msg);
            }
        }
    }


}
