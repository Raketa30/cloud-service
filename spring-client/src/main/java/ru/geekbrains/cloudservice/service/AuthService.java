package ru.geekbrains.cloudservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.AuthHandler;
import ru.geekbrains.cloudservice.dto.UserTo;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private AuthHandler authHandler;

    private boolean logged;
    private UserTo userTo;

    public AuthService() {
        this.logged = false;
    }

    public void userLogin(String username, String password) {
        authHandler.sendLoginRequest(username, password);
    }

    public void confirmLoginRequest(UserTo userFromRequest) {
        //вывести главное окно с именем польователя
        this.logged = true;
        this.userTo = userFromRequest;
        log.info("loogged {}", logged);
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
