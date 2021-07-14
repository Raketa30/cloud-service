package ru.geekbrains.cloudservice.client.api.nio;

import ru.geekbrains.cloudservice.client.api.Connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NIOConnector extends Connector {
    private SocketChannel client;

    public NIOConnector(int port){
        super(port);
    }

    @Override
    public void connect(String address, int port) throws IOException {
        client = SocketChannel.open(new InetSocketAddress(port));
        setSender(new NIOFileSender(client));
    }

    @Override
    public void disconnect() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
