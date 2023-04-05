package ru.geekbrains.march.chat.server;

import java.sql.*;

public class DbAuthenticationProvider implements AuthenticationProvider{

    private  static Connection connection; //  объявили соединение
    private static Statement stmt; // объявляем для создания запросов в бд

    @Override
    public String getNicknameByLoginAndPassword (String login, String password) {
        try (ResultSet rs = stmt.executeQuery("select nickname from nickname where login = '" + login + "' and password = '" + password + "';")) {
            while (rs.next()) {
                System.out.println();
                // rs. next =  двигает курсор
                return rs.getString(1);
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void changeNickname(String oldNickname, String newNickname) {
        throw new UnsupportedOperationException();
    }


    public static void connect () {
        try {
            Class.forName("org.sqlite.JDBC"); //  загрузка драйвера в память ->  срабатывает классический блок инициализации -> и он себя зарегистрировал в драйвер менеджере
            connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db"); // открытие соединения
            stmt = connection.createStatement(); //  создаем Statement

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Невозможно подключиться к БД");
        }
    }

    public static void disconnect (){ // закрывается в том же порядке,  в котором открывался
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
