package ru.geekbrains.cloudservice.server.api;

import java.io.IOException;

public interface Receiver {
    void receive() throws IOException;
}
