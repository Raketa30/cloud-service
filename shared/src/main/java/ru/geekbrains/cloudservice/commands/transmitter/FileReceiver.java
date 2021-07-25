package ru.geekbrains.cloudservice.commands.transmitter;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class FileReceiver {
    private SocketChannel socketChannel;
    private FileWriter fileWriter;
    private long size;

    public FileReceiver(SocketChannel socketChannel, FileWriter fileWriter, long size) {
        this.socketChannel = socketChannel;
        this.fileWriter = fileWriter;
        this.size = size;
    }

    public void receive() throws IOException {
        doTransfer();
    }

    private void doTransfer() throws IOException {
        this.fileWriter.transfer(socketChannel, this.size);
    }
}
