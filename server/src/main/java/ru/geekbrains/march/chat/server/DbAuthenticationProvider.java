package ru.geekbrains.march.chat.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbAuthenticationProvider implements AuthenticationProvider{
    private static final Logger log = LogManager.getLogger(DbAuthenticationProvider.class); // в каждом классе создаем свой логгер

    private  DbConnection dbConnection;


    @Override
    public void init() {
        dbConnection = new DbConnection(); //  при ините открываем
    }
    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {

        String query = String.format("select nickname from users where login = '%s' and password = '%s'",login,password);

//        String sql = "select nickname from users where login = 'Bob' and password = '100';";
//        try (ResultSet rs = dbConnection.getStmt().executeQuery(sql)) {
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {

            if (rs.next()) {

                // rs. next =  двигает курсор
                System.out.println(rs.getString("nickname"));
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();


        }
        return null;
    }

    @Override
    public void changeNickname(String oldNickname, String newNickname) {
        String query = String.format("update users set nickname = '%s' where nickname = '%s'",newNickname,oldNickname);
        try {
            //todo есть опасность наткнуться на неуникальный ник
            dbConnection.getStmt().executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @Override
    public boolean isNickBusy(String nickname) {
        String query = String.format("select id from users where nickname = '%s';", nickname);
        try (ResultSet rs = dbConnection.getStmt().executeQuery(query)) {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void shutdown() {
        dbConnection.close();
    }
}
