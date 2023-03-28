package ru.geekbrains.march.chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    ListView <String> clientList;

    @FXML
    TextArea msgArea;

    @FXML
    TextField msgField, usernameField;

    @FXML
    HBox loginPanel, msgPanel;


    //    socket это сетевое соединение
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username; //  клиент должен знать под каким логином он сидит



    public void setUsername (String username) {
        this.username = username;
        if (username != null) {
           loginPanel.setVisible(false);
           loginPanel.setManaged(false);
           msgPanel.setVisible(true);
           msgPanel.setManaged(true);
           clientList.setVisible(true);
           clientList.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUsername(null);
    }

    //  при на нажатии на кнопку логин
    public void login () {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        if (usernameField.getText().isEmpty()) {
           Alert alert = new Alert(Alert.AlertType.ERROR, "Имя пользователя не может быть пустым", ButtonType.OK);
           alert.showAndWait();
           return; // делаем return  чтобы не отправить серваку пустой логин
        }

        try {
            out.writeUTF("login " + usernameField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        // открываем сетевое соединение
        try {
            socket = new Socket("localhost", 8191);
            in = new DataInputStream(socket.getInputStream()); // чтение
            out = new DataOutputStream(socket.getOutputStream()); // запись

            // создаем поток, чтобы сервер и клиент работали параллельно

            Thread t = new Thread(() -> {
                try {
                    // цикл авторизации
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("login_ok")) {
                            System.out.println(msg);
                            setUsername(msg.split("\\s")[1]); //   разделение по пробелу \\s  на 2 части  и мы берем 1-ую часть
                            break;
                        }

                        if (msg.startsWith("login_failed")) {
                            System.out.println(msg);
                            String cause = msg.split("\\s", 2)[1];
                            msgArea.appendText(cause + "\n");
                        }
                    }

                    // цикл общения
                    while (true) {
                        String msg = in.readUTF();

                        if (msg.startsWith("/")) {
                            if (msg.startsWith("/clients_list")) {
                                String [] tokens = msg.split("\\s");
                                //  /clients_list Bob Jack Max

                                //  это  поток джава fx,  чтобы не было ошибки
                                Platform.runLater(()-> {
                                    clientList.getItems().clear(); //  все имена, которые были,  очистить
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientList.getItems().add(tokens[i]);
                                    }
                               });
                            }
                            continue;
                        }
                        msgArea.appendText(msg + "\n");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            });
            t.start();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно подключиться к серверу", ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus(); // запросить фокус, чтобы курсор был в этом окне текстфилд
        } catch (IOException e) {
            // покажем конкретную ошибку

            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение", ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void disconnect ()  {
         setUsername(null); // username сбрасываем
          try {
               if (socket != null) {
                   socket.close();
               }
           } catch ( IOException e) {
                e.printStackTrace();
           }
    }

    public void logoutBtnAction()  {
        try {
            socket.close();
            msgArea.clear();

        } catch (IOException e) {
            disconnect();
        }
    }

}




