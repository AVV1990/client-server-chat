module ru.geekbrains.march.chat.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.geekbrains.march.chat.client to javafx.fxml;
    exports ru.geekbrains.march.chat.client;
}