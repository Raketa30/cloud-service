package ru.geekbrains.cloudservice.client.api;

public abstract class Connector implements ClientConnector {
    private String address;
    private int port;
    private Sender sender;

    public Connector(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public Connector(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
