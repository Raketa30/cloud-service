package ru.geekbrains.cloudservice.client.api;

import ru.geekbrains.cloudservice.client.model.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientConnector {
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientConnector(String address, int port) {
        try (Socket socket = new Socket(address, port)) {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Path path) {
        FileInfo fileInfo = new FileInfo(path);
        try {
            dos.writeUTF(fileInfo.getFilename());
            dos.writeLong(fileInfo.getFileSize());
            Files.copy(path, dos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
