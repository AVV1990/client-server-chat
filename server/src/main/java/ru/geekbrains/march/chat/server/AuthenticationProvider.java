package ru.geekbrains.march.chat.server;

public interface AuthenticationProvider {

    void init (); // подготовься
    String getNicknameByLoginAndPassword (String login, String password);

    void changeNickname (String oldNickname, String newNickname);
    boolean isNickBusy (String nickname);

    void shutdown (); // заверши свою работу

}
