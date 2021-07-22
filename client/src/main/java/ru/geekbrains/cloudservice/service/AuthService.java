package ru.geekbrains.cloudservice.service;


import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.api.AuthHandler;
import ru.geekbrains.cloudservice.dto.UserTo;

@Slf4j
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

    public synchronized void userLogin(String username, String password) {
        authHandler.sendLoginRequest(username, password);
    }

    public synchronized void confirmLoginRequest(UserTo userFromRequest) {
        //вывести главное окно с именем польователя
        this.logged = true;
        this.userTo = userFromRequest;
        log.info("loogged {}", logged);
    }

    public void registerUser(String username, String password) {
        authHandler.sendRegistrationRequest(username, password);
    }

    public synchronized boolean isLogged() {
        return logged;
    }

    public UserTo getUserTo() {
        return userTo;
    }
}
