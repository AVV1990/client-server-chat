package ru.geekbrains.march.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

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
    public ClientHandler (Server server,Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream (socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread (()-> {
            // try и catch вынесли за пределы while,  чтобы при отключении клиента в консоль не выпадала ошибка
            try {
                int countMsg = 0;
                while(true) { // будет 2 цикла - 1 цикл это авторизации
                    String msg = in.readUTF(); // ждем сообщение
                    if (msg.startsWith("login") ) {
                        String usernameFromLogin = msg.split("\\s") [1]; //  у клиентам запрашиваем имя, которое он указал
                        if (server.isNickBusy(usernameFromLogin)) {
                            sendMessage("login_failed Current nickname is already used");
                            continue;
                        }


                        username = usernameFromLogin; //  если он не занят, то присваиваем его
                        sendMessage("login_ok " + username);
                        server.subscribe(this);//  после авторизации просим подписать клиента на рассылку
                        break; // после того, как  залогинился, выходим из цикла авторизации
                    }
                }
                // 2 ой цикл общения с клиентом
                while(true){
                    String message =  in.readUTF();


                    if (message.startsWith("/")) {
                        String[] msg = message.split("\\s", 3);

                        // дом. задание: подсчет количества сообщений
                        if (msg[0].equals("/stat")) {
                            out.writeUTF("Количество сообщений: " + countMsg);
                            continue;
                        }

                        // дом. задание: если от клиента приходит сообщение вида "who_am_i",  сервер отвечает этмоу клиенту его имя
                        if (msg[0].equals("/who_am_i")) {
                            out.writeUTF("you are " + username);
                            continue;
                        }

                        if (msg[0].equals("/w")) {

                            System.out.println("Вошли в цикл с отсылкой личного сообщения");
                            server.sendPrivateMsg(this, msg[2], msg[1]);
                            continue;
                        }

                        //  дом. задание: если от клиента приходит сообщение вида "exit",  то клиент отключается от сервера
                        if (msg[0].equals("/exit")) {
                            socket.close();
                            break;
                        }


                    }
                    countMsg++;
                    server.broadcastMassage( username + ": "+ message); //  когда сообщение приходит, сервер, разошли это сообщение абсолютно всем

                }

            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect(); // если клиент отключился, то со стороны сервера закрываем соединение, чтобы он не тратил ресурсы
            }
        }).start();
    }

    public  void sendMessage (String message) throws IOException {
        out.writeUTF(message);
    }

    public void disconnect () {
        server.unsubscribe(this); //  сервер, хотим  отписаться от рассылки
        if (socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






}
