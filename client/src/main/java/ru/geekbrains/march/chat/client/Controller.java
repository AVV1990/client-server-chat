package ru.geekbrains.march.chat.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea msgArea;

    @FXML
    TextField msgField;


    //    socket это сетевое соединение
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // открываем сетевое соединение
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream()); // чтение
            out = new DataOutputStream(socket.getOutputStream()); // запись

            // создаем поток, чтобы сервер и клиент работали паралельно

            Thread t = new Thread(() -> {
                try {

                    while (true) {
                        String msg = in.readUTF();
                        msgArea.appendText(msg+ "\n");
                    }
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();

        } catch (IOException e) {
            throw new RuntimeException("Unable to connect to server [localhost: 8189]");
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
        } catch (IOException e) {
            // покажем конкретную ошибку

            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение", ButtonType.OK);
            alert.showAndWait();
        }
    }
}