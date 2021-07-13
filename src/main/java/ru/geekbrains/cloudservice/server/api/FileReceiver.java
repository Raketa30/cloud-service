package ru.geekbrains.cloudservice.server.api;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class FileReceiver implements Receiver {

    private final FileWriter fileWriter;
    private final long size;

    public FileReceiver(FileWriter fileWriter, long size) {
        this.fileWriter = fileWriter;
        this.size = size;
    }

    @Override
    public void receive(SocketChannel socketChannel) throws IOException {
        try {
            this.fileWriter.transfer(socketChannel, size);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.fileWriter.close();
        }

    }
}
