package ru.geekbrains.march.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Locale;

public class ClientHandler {

// этот класс обработчик клиентов


    private Server server; //  этот обработчик клиентов крутится на каком-то сервере
    private Socket socket;  //      соединение
    private DataInputStream in; //  входящий поток
    private DataOutputStream out; // исходящий поток
    private String username; //  имя пользователя


   public String getUsername() {
        return username;
    }

    //     когда клиент подколючается, то у нас будет конструктлор, который примет  на вход
    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            // try и catch вынесли за пределы while,  чтобы при отключении клиента в консоль не выпадала ошибка
            try {
                int countMsg = 0;
                while (true) { // будет 2 цикла - 1 цикл это авторизации

                    String msg = in.readUTF(); // ждем сообщение
                    if (msg.startsWith("login ")) {
                        //  login Bob 100 - такое сообщение приходит на сервак

                        String[] tokens = msg.split("\\s+");
                        System.out.println(Arrays.toString(tokens));

                        if (tokens.length != 3) {
                            sendMessage("login_failed Введите имя пользователя и пароль");
                            continue;
                        }
                        String login = tokens[1];
                        String password = tokens[2];


                        String userNickname = server.getAuthenticationProvider().getNicknameByLoginAndPassword(login,password);

                        if (userNickname == null) {
                            sendMessage("login_failed Введен некорректный логин/пароль");
                            continue;
                        }

                        if (server.isUserOnLine(userNickname)) {
                            sendMessage("login_failed Учетная запись уже используется");
                            continue;
                        }

                        username = userNickname; //  если он не занят, то присваиваем его
                        sendMessage("login_ok " + login + " " + username);
                        server.subscribe(this);//  после авторизации просим подписать клиента на рассылку
                        break; // после того, как  залогинился, выходим из цикла авторизации
                    }
                }
                // 2 ой цикл общения с клиентом
                while (true) {

                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        executeCommand(msg);
                        continue;
                    }


                    //if (message.startsWith("/")) {
//                        String[] msg = message.split("\\s", 3);


//                        if (msg[0].equals("/stat")) {
//                            out.writeUTF("Количество сообщений: " + countMsg);
//                            continue;
//                        }

//                        if (msg[0].equals("/who_am_i")) {
//                            out.writeUTF("you are " + username);
//                            continue;
//                        }

//                        if (msg[0].equals("/w")) {
                    //      /w Bob Hello, Bob!

//                            System.out.println("Вошли в цикл с отсылкой личного сообщения");
//                            server.sendPrivateMsg(this, msg[2], msg[1]);
//                            continue;
//                        }

//                        if (msg[0].equals("/exit")) {
//                            socket.close();
//                            break;
//                        }


                    //  }
                    // countMsg++;
                    server.broadcastMassage(username + ": " + msg); //  когда сообщение приходит, сервер, разошли это сообщение абсолютно всем

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect(); // если клиент отключился, то со стороны сервера закрываем соединение, чтобы он не тратил ресурсы
            }
        }).start();
    }

    private void executeCommand(String cmd) {
        // /w Bob  Неllо, Bob!!!!
        if (cmd.startsWith("/w")) {
            String[] tokens = cmd.split("\\s+", 3); //       \\s+ убирает возможные пробелы
            if (tokens.length != 3) {
                sendMessage("Server: Введена некорректная команда ");
                return;
            }
            server.sendPrivateMassage(this, tokens[1], tokens[2]);
            return;
        }
        // /change_nik myNewNickname
        if (cmd.startsWith("/change_niсk")) {
            String[] tokens = cmd.split("\\s+");  //       \\s+ убирает возможные пробелы
            if (tokens.length != 2) {
                sendMessage("Server: Введена некорректная команда ");
                return;
            }
            String newNickname = tokens[1];
            if (server.getAuthenticationProvider().isNickBusy(newNickname)) {
                sendMessage("Server: Такой ник уже занят");
                return;
            }
            server.getAuthenticationProvider().changeNickname(username, newNickname);
            username = newNickname;
            sendMessage("Server: Вы изменили никнейм на " + newNickname);
            server.broadcastClientsList();

        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {
        server.unsubscribe(this); //  сервер, хотим  отписаться от рассылки
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
