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

    public void setUsername(String username) {
        this.username = username;
    }

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
                        if (server.isUserOnLine(usernameFromLogin)) {
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
                    System.out.println("Это команда");
                    String msg =  in.readUTF();
                    if (msg.startsWith("/")) {
                      executeCommand (msg);
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
                    server.broadcastMassage( username + ": "+ msg); //  когда сообщение приходит, сервер, разошли это сообщение абсолютно всем

                }

            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect(); // если клиент отключился, то со стороны сервера закрываем соединение, чтобы он не тратил ресурсы
            }
        }).start();
    }

    private void executeCommand(String msg) {
        System.out.println("Вошли в метод выполнения команды");
        // /w Bob  Неllо, Bob!!!!

        String[] tokens = msg.split("\\s", 3);
        String cmd = tokens[0];
        if (cmd.equals("/w")) {
            server.sendPrivateMassage(this, tokens[1], tokens[2]);
            return;
        }
        if (cmd.equals("/change_nik")) {
            System.out.println("Вошли в иф изменения ника");
            // /change_nik myNewNickname
            server.changeNick(this, tokens[1], username);
        }
    }

    public  void sendMessage (String message){
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect ()  {
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
