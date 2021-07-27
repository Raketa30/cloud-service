package ru.geekbrains.cloudservice.transmitter;

import ru.geekbrains.cloudservice.commands.Constants;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class FileSender {

    private final SocketChannel socketChannel;

    public FileSender(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void transfer(FileChannel fileChannel, long position, long size) throws IOException {
        while(position < size) {
            position += fileChannel.transferTo(position, Constants.TRANSFER_MAX_SIZE, this.socketChannel);
        }
    }

}
