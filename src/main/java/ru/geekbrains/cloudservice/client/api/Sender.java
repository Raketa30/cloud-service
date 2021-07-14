package ru.geekbrains.cloudservice.client.api;

import java.nio.file.Path;

public interface Sender {
    void send(Path path);
}
