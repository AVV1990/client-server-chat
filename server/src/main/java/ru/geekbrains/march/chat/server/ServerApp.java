package ru.geekbrains.march.chat.server;


import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class ServerApp {
    public static void main(String[] args)  {
        new Server(8191);
    }
}