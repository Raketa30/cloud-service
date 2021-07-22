package ru.geekbrains.cloudservice;

import ru.geekbrains.cloudservice.api.NettyServer;

public class ServerStarter {
    public static void main(String[] args) {
        new NettyServer(23232).run();
    }
}
