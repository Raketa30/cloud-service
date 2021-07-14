package ru.geekbrains.cloudservice.server.api;

import ru.geekbrains.cloudservice.constants.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileWriter {
    private FileChannel fileChannel;

    public FileWriter(String path) throws IOException {
        this.fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    }

    public void transfer(SocketChannel socketChannel, long bytes) throws IOException {
        long position = 0L;
        while (position < bytes) {
            position += this.fileChannel.transferFrom(socketChannel, position, Constants.MAX_TRANSFER_VALUE);
        }
    }

    public int write(ByteBuffer buffer, long position) throws IOException {
        int byteWritten = 0;
        while(buffer.hasRemaining()) {
            byteWritten += this.fileChannel.write(buffer, position + byteWritten);
        }

        return byteWritten;
    }

    public void close() throws IOException {
        this.fileChannel.close();
    }

}
