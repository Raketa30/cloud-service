package ru.geekbrains.cloudservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.AuthHandler;
import ru.geekbrains.cloudservice.dto.UserTo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private AuthHandler authHandler;

    private String userFolderPath;
    private UserTo userTo;

    private volatile boolean loginConfirm;
    private volatile boolean loginDecline;
    private volatile boolean registrationConfirm;
    private volatile boolean registrationDecline;

    public AuthService() {
        this.loginConfirm = false;
        this.loginDecline = false;

        this.registrationConfirm = false;
        this.registrationDecline = false;
    }

    public void userLogin(String username, String password) {
        authHandler.sendLoginRequest(username, password);
    }

    public void confirmLoginRequest(UserTo userFromRequest) {
        //вывести главное окно с именем польователя
        this.loginConfirm = true;
        this.userTo = userFromRequest;
        String userDirectory = getUserFolderPath() + "/GeekbrainsCloud-" + userTo.getUsername();
        createLocalUserDirectory(userDirectory);
        log.info("logged {}", loginConfirm);
    }

    public void registerUser(String username, String password) {
        authHandler.sendRegistrationRequest(username, password);
    }

    public boolean isLoginConfirm() {
        return loginConfirm;
    }

    public boolean isLoginDecline() {
        return loginDecline;
    }

    public void declineLogin() {
        loginDecline = true;
    }

    public void confirmRegistration(UserTo usr) {
        registrationConfirm = true;
    }

    public void createLocalUserDirectory(String userDirectory) {
        if (Files.exists(Paths.get(userDirectory))) {
            return;
        }

        try {
            Files.createDirectory(Paths.get(userDirectory));
            File file = new File("spring-client/settings.txt");
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.write(userTo.getUsername() + " : " + userDirectory + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void declineRegistration() {
        registrationDecline = true;
    }

    public boolean isRegistrationConfirm() {
        return this.registrationConfirm;
    }

    public boolean isRegistrationDecline() {
        return registrationDecline;
    }

    public String getUserFolderPath() {
        return userFolderPath;
    }

    public void setUserFolderPath(String userFolderPath) {
        this.userFolderPath = userFolderPath;
    }

    public void declineLoginRequest() {
        this.loginDecline = true;
    }

    public UserTo getUserTo() {
        return userTo;
    }
}
