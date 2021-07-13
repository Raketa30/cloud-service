package ru.geekbrains.cloudservice.client.api.nio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileReader {
    private final FileChannel channel;
    private final NIOClientConnector sender;

    public FileReader(NIOClientConnector sender, Path path) throws IOException {
        this.sender = sender;
        this.channel = FileChannel.open(path, StandardOpenOption.READ);
    }

    public void read() throws IOException {
        try {
            transfer();
        } finally {
            close();
        }
    }

    private void transfer() throws IOException {
        this.sender.transfer(this.channel, 0l, this.channel.size());
    }

    public void close() throws IOException {
        this.channel.close();
        this.sender.close();
    }
}
