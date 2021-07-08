package ru.geekbrains.cloudservice.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private String directory = "server_directory";
    private final byte[] buffer;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Socket socket;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.buffer = new byte[1024];
    }

    @Override
    public void run() {
        try {
            while (true) {
                String filename = dis.readUTF();
                long fileSize = dis.readLong();
                try (FileOutputStream fos = new FileOutputStream(directory + "/" + filename)) {
                    for (int i = 0; i < (fileSize + 1023) / 1024; i++) {
                        int read = dis.read(buffer);
                        fos.write(buffer, 0, read);
                    }

//                    dos.writeUTF("file: " + filename + " received");
                    dos.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception while read from ClintHandler");
        }
    }
}
