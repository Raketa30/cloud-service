package ru.geekbrains.cloudservice.client.api.nio;

import ru.geekbrains.cloudservice.constants.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NIOClientConnector {
    private SocketChannel client;

    public NIOClientConnector(int port){
        try {
            client = SocketChannel.open(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void transfer (FileChannel channel, long position, long size) throws IOException {
        while(position < size) {
            position += channel.transferTo(position, Constants.MAX_TRANSFER_VALUE, this.client);
        }
    }

    public SocketChannel getClient() {
        return client;
    }

    public void close() throws IOException {
        this.client.close();
    }
}
