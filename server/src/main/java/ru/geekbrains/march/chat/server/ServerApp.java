package ru.geekbrains.march.chat.server;


import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {

            System.out.println("Сервер запущен на порту 8189. Ожидаем подключение клиента...");

            Socket socket = serverSocket.accept();

            // Стандартные потоки завернули в Дaта потоки, продвинутые потоки
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Клиент подключился");

//        пока соединение не отвалилось, принимаем (ЧИТАЕМ) сообщения ДО ТЕХ ПОР , ПОКА  КЛИЕНТ НЕ ПРИСЛАЛ СООБЩЕНИЕ

            int countMsg = 0;

            try {
                while (true) {
                    String msg = in.readUTF();
                    if (msg.equals("/stat")) {
                        out.writeUTF("Количество сообщений -" + countMsg);
                    } else {
                        countMsg++;
                        System.out.println("Количество сообщений -" + countMsg);

//             вначале было так: байты (int) преобразовываем в символы (char)x - ASCII, но пришлось поменять на  String msg из-за того что кириллицу не поддерживала
                        System.out.println(msg);
                        // socket дай мне исход. поток к клиенту и я туда положу сообщение, который только что прочитал и отправлю обратно
                        out.writeUTF("ECHO " + msg);

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
//        serverSocket.close();

    }
}