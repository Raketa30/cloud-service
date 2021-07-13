package ru.geekbrains.cloudservice.server.api;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Receiver {
    void receive(SocketChannel socketChannel) throws IOException;
}
