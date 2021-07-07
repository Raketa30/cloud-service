package ru.geekbrains.cloudservice.server;

import ru.geekbrains.cloudservice.util.ConsoleHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CloudServer {
    public CloudServer() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(8985)) {
            ConsoleHelper.printMessage("Connection to server..");

            while(true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, "folder_name");
                ConsoleHelper.printMessage("Client connected..");
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new CloudServer();
    }
}
