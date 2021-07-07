package ru.geekbrains.cloudservice.client;

import ru.geekbrains.cloudservice.util.ConsoleHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudClient {
    private Socket socket;

    public CloudClient(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            ConsoleHelper.printMessage("connected");
            while (true) {
                String source = ConsoleHelper.getSource();
                send(source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CloudClient("localhost", 8985);
    }

    public void send(String source) {
        Path path = Paths.get(source);

        if (Files.isRegularFile(path)) {
            try (OutputStream outputStream = socket.getOutputStream();
                 InputStream inputStream = Files.newInputStream(path)) {

                outputStream.write(path.getFileName().toString().getBytes());
                byte[] buffer = new byte[256];
                while (inputStream.available() > 0) {
                    outputStream.write(inputStream.read(buffer));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
