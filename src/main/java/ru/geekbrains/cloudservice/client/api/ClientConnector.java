package ru.geekbrains.cloudservice.client.api;

import java.io.IOException;

public interface ClientConnector {
    void connect(String address, int port) throws IOException;

    void disconnect();
}
