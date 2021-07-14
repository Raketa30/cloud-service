package ru.geekbrains.cloudservice.client.api.io;

import ru.geekbrains.cloudservice.client.api.Connector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class IOConnector extends Connector {
    private Socket socket;

    public IOConnector(String address, int port) {
        super(address, port);
    }

    @Override
    public void connect(String address, int port) throws IOException {
        socket = new Socket(address, port);
        setSender(new IOFileSender(new DataOutputStream(socket.getOutputStream())));
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
