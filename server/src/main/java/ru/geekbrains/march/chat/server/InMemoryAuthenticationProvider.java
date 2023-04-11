//package ru.geekbrains.march.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//
///*
//
//PRAGMA foreign_keys = 0;
//
//CREATE TABLE sqlitestudio_temp_table AS SELECT *
//                                          FROM nickname;
//
//DROP TABLE nickname;
//
//CREATE TABLE nickname (
//    login    TEXT,
//    password TEXT,
//    nickname TEXT PRIMARY KEY
//                  UNIQUE
//);
//
//INSERT INTO nickname (
//                         login,
//                         password
//                     )
//                     SELECT login,
//                            password
//                       FROM sqlitestudio_temp_table;
//
//DROP TABLE sqlitestudio_temp_table;
//
//
// */
//
////public class InMemoryAuthenticationProvider implements AuthenticationProvider {
//    // это класс,  где логины и пароли хранятся
//
////    public class User {
////        private String login;
////        private String password;
////        private String nickname;
////
////        public User(String login, String password, String nickname) {
////            this.login = login;
////            this.password = password;
////            this.nickname = nickname;
////        }
////    }
////
////    private List <User> users; //  у него есть список useroв
//
////    public InMemoryAuthenticationProvider() {
////        this.users = new ArrayList<>(Arrays.asList(
////                new User("Bob", "100", "MegaBob"),
////                new User("Jack", "100", "Mystic"),
////                new User("John", "100", "MegaJohn")
////        ));
////    }
//
////    @Override
////    public String getNicknameByLoginAndPassword(String login, String password) {
////        for (User u : users) {
////            if (u.login.equals(login)&& u.password.equals(password)) {
////                return u.nickname;
////            }
////        }
////        return null;
////    }


//       @Override
//       public void changeNickname(String oldNickname,String newNickname){
//            for(User u:users){
//                 if(u.nickname.equals(oldNickname)){
//                     u.nickname=newNickname;
//                      return;
//                  }
//             }
//        }

//
//
////    @Override
////    public void changeNickname(String oldNickname, String newNickname) {
////        for (User u : users) {
////            if (u.equals(oldNickname)) {
////                u.nickname = newNickname;
////                return;
////            }
////        }
////
////    }
//
//
//
//
////}
