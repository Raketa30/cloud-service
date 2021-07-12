package ru.geekbrains.cloudservice.server.ioserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CloudServer {
    public CloudServer() {
        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            System.out.println("Server started");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted");
                try {
                    new Thread(new ClientHandler(socket)).start();
                } catch (Exception e) {
                    System.out.println("Connection Error");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CloudServer();
    }
}
