package ru.geekbrains.cloudservice.service;


import ru.geekbrains.cloudservice.api.AuthHandler;
import ru.geekbrains.cloudservice.dto.UserTo;

public class AuthService {
    private AuthHandler authHandler;
    private boolean logged;
    private UserTo userTo;

    public AuthService() {
        this.logged = false;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public void userLogin(String username, String password) {
        authHandler.sendLoginRequest(username, password);
    }

    public void confirmLoginRequest(UserTo userFromRequest) {
        //вывести главное окно с именем польователя
        this.logged = true;
        this.userTo = userFromRequest;
    }

    public void registerUser(String username, String password) {
        authHandler.sendRegistrationRequest(username, password);
    }

    public boolean isLogged() {
        return logged;
    }

    public UserTo getUserTo() {
        return userTo;
    }
}
