package ru.geekbrains.cloudservice.service;


import ru.geekbrains.cloudservice.api.AuthHandler;

public class AuthService {
    private AuthHandler authHandler;

    public AuthService(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public void userLogin(String username, String password) {
        authHandler.sendLoginRequest(username, password);
    }
}
