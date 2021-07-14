package ru.geekbrains.cloudservice.client.api;

import java.nio.file.Path;

public abstract class Connector implements ClientConnector {
    private String address;
    private int port;
    private Sender sender;

    public Connector(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void send(Path path) {
        sender.send(path);
    }
}
