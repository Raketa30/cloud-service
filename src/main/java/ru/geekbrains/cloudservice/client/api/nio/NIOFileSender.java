package ru.geekbrains.cloudservice.client.api.nio;

import ru.geekbrains.cloudservice.client.api.Sender;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

public class NIOFileSender implements Sender {
    private SocketChannel client;

    public NIOFileSender(SocketChannel client) {
        this.client = client;

    }

    @Override
    public void send(Path path) {
        try {
            FileReader fileReader = new FileReader(this, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketChannel getClient() {
        return client;
    }
}
