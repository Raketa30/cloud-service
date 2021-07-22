package ru.geekbrains.cloudservice.configuration;

import ru.geekbrains.cloudservice.api.NettyConnector;
import ru.geekbrains.cloudservice.service.AuthService;

public class MainConfiguration {

    private NettyConnector nettyConnector;
    private AuthService authService;

    public MainConfiguration(int port, String address) {
        authService = new AuthService();
        initServer(port, address);
        initServices();
    }

    private void initServer(int port, String address) {
        nettyConnector = new NettyConnector(address, port);
    }

    private void initServices() {
        authService.setAuthHandler(nettyConnector.getAuthHandler());
        nettyConnector.getAuthHandler().setAuthService(authService);
    }
}
