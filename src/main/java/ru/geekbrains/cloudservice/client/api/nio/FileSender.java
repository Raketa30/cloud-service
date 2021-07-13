package ru.geekbrains.cloudservice.client.api.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class FileSender {
    private SocketChannel client;

    public FileSender(String address, int port) throws IOException {
        client = SocketChannel.open(new InetSocketAddress(port));
    }

    public void transfer (FileChannel channel, long position, long size) throws IOException {
        while(position < size) {
            position += channel.transferTo(position, 1024, client);
        }
    }

    public SocketChannel getClient() {
        return client;
    }

    public void close() throws IOException {
        this.client.close();
    }
}
