package ru.geekbrains.cloudservice.client.api.nio;

import ru.geekbrains.cloudservice.client.api.Connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NIOConnector extends Connector {
    private SocketChannel client;

    public NIOConnector(String address, int port){
        super(address, port);
        try {
            connect(address, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(String address, int port) throws IOException {
        client = SocketChannel.open(new InetSocketAddress(address, port));
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
