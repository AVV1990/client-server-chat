package ru.geekbrains.march.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticationProvider {
    // это класс,  где логины и пароли хранятся

    public class User {
        private String login;
        private String password;
        private String nickname;

        public User(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List <User> users; //  у него есть список useroв

    public InMemoryAuthenticationProvider() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("Bob", "100", "MegaBob"),
                new User("Jack", "100", "Mystic"),
                new User("John", "100", "MegaBob")
        ));
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login)&& u.password.equals(password)) {
                return u.nickname;
            }
        }
        return null;
    }


    @Override
    public void changeNickname(String oldNickname, String newNickname) {
        for (User u : users) {
            if (u.equals(oldNickname)) {
                u.nickname = newNickname;
                return;
            }
        }

    }




}
