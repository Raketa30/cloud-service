package ru.geekbrains.cloudservice.server.api;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class FileReceiver implements Receiver {

    private final SocketChannel socketChannel;
    private final FileWriter fileWriter;
    private final long size;

    public FileReceiver(SocketChannel socketChannel, FileWriter fileWriter, long size) {
        this.socketChannel = socketChannel;
        this.fileWriter = fileWriter;
        this.size = size;
    }

    @Override
    public void receive() throws IOException {
        try {
            doTransfer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.fileWriter.close();
        }

    }

    private void doTransfer() throws IOException {
        this.fileWriter.transfer(socketChannel, this.size);
    }
}
