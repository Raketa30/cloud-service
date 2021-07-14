package ru.geekbrains.cloudservice.client.api.nio;

import ru.geekbrains.cloudservice.util.MyLogger;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileReader {
    private FileChannel fileChannel;
    private NIOFileSender nioFileSender;

    public FileReader(NIOFileSender nioFileSender, Path path) throws IOException {
        this.nioFileSender = nioFileSender;
        this.fileChannel = FileChannel.open(path, StandardOpenOption.READ);
    }

    public void read() {
        try {
            transfer();
        } catch (IOException e) {
            MyLogger.logError("Cannot transfer file");
        }
    }

    private void transfer() throws IOException {
        this.nioFileSender.transfer(this.fileChannel, 0L, this.fileChannel.size());
    }

}
