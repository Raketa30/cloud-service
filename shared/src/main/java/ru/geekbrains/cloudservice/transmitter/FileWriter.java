package ru.geekbrains.cloudservice.transmitter;

import ru.geekbrains.cloudservice.commands.Constants;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileWriter {
    private final FileChannel fileChannel;

    public FileWriter(Path path) throws IOException {
        this.fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    }

    public void transfer(SocketChannel socketChannel, long bytes) throws IOException {
        long position = 0L;

        while(position < bytes) {
            position += this.fileChannel.transferFrom(socketChannel, position, Constants.TRANSFER_MAX_SIZE);
        }
    }
}
