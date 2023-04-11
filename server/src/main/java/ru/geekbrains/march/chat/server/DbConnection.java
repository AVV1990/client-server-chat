package ru.geekbrains.march.chat.server;

import java.sql.*;

public class DbConnection {

    private Connection connection; //  объявили соединение
    private Statement stmt; // объявляем для создания запросов в бд

    public Statement getStmt() {
        return stmt;
    }

    public DbConnection() {
        try {

            Class.forName("org.sqlite.JDBC"); //  загрузка драйвера в память ->  срабатывает классический блок инициализации -> и он себя зарегистрировал в драйвер менеджере
            this.connection = DriverManager.getConnection("jdbc:sqlite:database.db"); // открытие соединения
            this.stmt = connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Невозможно подключиться к БД");
        }
    }


    public void close (){ // закрывается в том же порядке,  в котором открывался
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(connection != null) { //  проверка обязательна: а не null  ли то,что мы закрываем
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


}
