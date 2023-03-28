package ru.geekbrains.march.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
    public synchronized void subscribe(ClientHandler clientHandler)  {
        clients.add(clientHandler);
        broadcastMassage("Клиент " + clientHandler.getUsername() + "  вошел в чат");
        broadcastClientsList();
    }

    // отключаем от рассылки, чтобы не получать сообщения от сервера
    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMassage("Клиент " + clientHandler.getUsername() + "  вышел из чата");
        broadcastClientsList();
    }

    // broadcastMassage - метод, который позволяет разослать всем клиентам сообщение

    public synchronized void broadcastMassage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }


    public synchronized void sendPrivateMassage(ClientHandler sender, String receiverUsername, String message)  {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(receiverUsername)) {
                c.sendMessage("От: " + sender.getUsername() + " Cообщение: " + message);
                sender.sendMessage("Пользователю: " + receiverUsername + " Cообщение: " + message);
                return;
            }
        }
        sender.sendMessage("Невозможно отправить сообщение пользователю: " + receiverUsername + " Такого пользователя нет в сети");
    }

    public synchronized void changeNick (ClientHandler username, String newUsername) {
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(username)) {
                 // /change_nik myNewNickname - такая команда
                c.getUsername().replaceAll(username.getUsername(), newUsername);
                broadcastMassage("Клиент " + username.getUsername() + " изменил ник на: " + newUsername);
            }
        }

    }

    public synchronized boolean isUserOnLine (String username) {
       for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                return true;
            }
        }
        return false; // если прошлись по всем клиентам, и ни у кого не нашли такой ник, то говорим, что не занят
    }

    public synchronized void broadcastClientsList()  {
        StringBuilder stringBuilder = new StringBuilder("/clients_list "); //  внутренний массив с символами
        for (ClientHandler c : clients) {
            stringBuilder.append(c.getUsername()).append(" ");
        }
        stringBuilder.setLength(stringBuilder.length()-1); //  убираем в конце пробел
        // /clients_list Bob Jack Max- на выходе получим
        String clientsList = stringBuilder.toString(); //  преобразуем в строку
        for (ClientHandler clientHandler : clients) { // шлем каждому список клиентов
            clientHandler.sendMessage(clientsList);
        }
    }

     // public void sendPrivateMsg(ClientHandler fromNickName, String msg, String toNickName) throws IOException{
        //     /w Bob Hello, Bob!
//
//        (for (ClientHandler client: clients) {
//            if (toNickName.equals(client.getUsername())){
//                client.sendMessage(fromNickName.getUsername() + " ---> "+ toNickName + " " + msg);
//                fromNickName.sendMessage (fromNickName.getUsername() + " ---> "+ toNickName + " " + msg);
//                return;
//            }
//        }
//        fromNickName.sendMessage("Невозможно отправить сообщение пользователю: " + toNickName + " Такого пользователя нет в сети");
//    }

}
