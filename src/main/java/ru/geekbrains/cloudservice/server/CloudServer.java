package ru.geekbrains.cloudservice.server;

import ru.geekbrains.cloudservice.util.ConsolePrinter;

import java.io.IOException;
import java.net.ServerSocket;

public class CloudServer {
    public CloudServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8584);
        ConsolePrinter.printMessage("Connection to server..");

        while(true) {
            
        }
    }
}
