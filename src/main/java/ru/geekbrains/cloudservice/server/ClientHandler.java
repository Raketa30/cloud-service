package ru.geekbrains.cloudservice.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private Path folderName;


    public ClientHandler(Socket clientSocket, String folderName) {
        this.socket = clientSocket;
        createFolder(folderName);
        receive();
    }

    private void createFolder(String name) {
        File folder = new File(name);
        if (!folder.exists()) {
            folder.mkdir();
        }
        folderName = Paths.get(folder.getAbsolutePath());
    }

    @Override
    public void run() {

    }

    private void receive() {
        new Thread(() -> {
            while (true) {
                try (InputStream inputStream = socket.getInputStream()) {
                    String filename = String.valueOf(inputStream.read());
                    System.out.println(filename);

                    Path file = Paths.get(filename).getFileName().relativize(folderName);

                    try (OutputStream outputStream = Files.newOutputStream(file)) {

                        byte[] buffer = new byte[256];
                        while (inputStream.available() > 0) {
                            outputStream.write(inputStream.read(buffer));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
