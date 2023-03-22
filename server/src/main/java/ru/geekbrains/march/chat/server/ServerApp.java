package ru.geekbrains.march.chat.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {

            System.out.println("Сервер запущен на порту 8189. Ожидаем подключение клиента...");

            Socket socket = serverSocket.accept();
            System.out.println("Клиент подключился");

//        пока соединение не отвалилось, принимаем (ЧИТАЕМ) сообщения ДО ТЕХ ПОР , ПОКА  КЛИЕНТ НЕ ПРИСЛАЛ СООБЩЕНИЕ
            int x;
            while ((x = socket.getInputStream().read()) != -1) {
//             далее байты (int) преобразовываем в символы (char)x - ASCII
                System.out.print((char) x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//        serverSocket.close();

}