package ru.geekbrains.march.chat.server;


import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ServerApp {
    private static final Logger log = LogManager.getLogger(ServerApp.class); // в каждом классе создаем свой логгер


    public static void main(String[] args)  {

        new Server(8191);
    }
}