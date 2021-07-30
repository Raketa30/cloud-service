package ru.geekbrains.cloudservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientHandler;
import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
@Service
public class ClientAuthService {
    @Autowired
    private ClientHandler clientHandler;

    private String userFolderPath;
    private UserTo userTo;

    //флаги хрень - потом переписать
    private volatile boolean loginConfirm;
    private volatile boolean loginDecline;
    private volatile boolean registrationConfirm;
    private volatile boolean registrationDecline;

    public ClientAuthService() {
        this.loginConfirm = false;
        this.loginDecline = false;

        this.registrationConfirm = false;
        this.registrationDecline = false;
    }

    public void userLogin(String username, String password) {
        AbstractMessage abstractMessage = new User(username, password);
        clientHandler.sendRequestToServer(new RequestMessage(new AuthRequest(AuthRequestType.LOGIN), abstractMessage ));
    }


    public void registerUser(String username, String password) {
        AbstractMessage abstractMessage = new User(username, password);
        clientHandler.sendRequestToServer(new RequestMessage(new AuthRequest(AuthRequestType.REGISTRATION),abstractMessage) );
    }

    public void confirmLoginRequest(AbstractMessage userFromRequest) {
        //вывести главное окно с именем польователя
        this.loginConfirm = true;
        this.userTo = (UserTo)userFromRequest;
        Optional<String> optionalPath = findUserFolderPath();
        optionalPath.ifPresent(s -> this.userFolderPath = s);
        log.info("logged {}", loginConfirm);
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

    public void confirmRegistration() {
        registrationConfirm = true;
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

    public Path getUserFolderPath() {
        return Paths.get(userFolderPath);
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

    //ищем папку пользователя в файле настроек
    public Optional<String> findUserFolderPath() {
        if (userTo == null) {
            return Optional.empty();
        }
        try (Scanner scanner = new Scanner(new File("spring-client/settings.txt"))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] credentials = line.split(" : ");

                if (credentials[0].equals(userTo.getUsername())) {
                    setUserFolderPath(credentials[1]);
                    return Optional.of(credentials[1]);
                }
            }
        } catch (IOException e) {
            log.warn("File setting not found");
        }
        return Optional.empty();
    }

    //создаем папку пользователя при успешной регистрации
    public void createLocalUserDirectory(String pickedDirectory) {
        String path = pickedDirectory + "/GeekbrainsCloud-" + userTo.getUsername();
        if (Files.exists(Paths.get(path))) {
            setUserFolderPath(pickedDirectory + "/" + userTo.getUsername());
            return;
        }

        try {
            Files.createDirectory(Paths.get(path));
            setUserFolderPath(path);

            File file = new File("spring-client/settings.txt");
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.write(userTo.getUsername() + " : " + path + "\n");
            }

        } catch (IOException e) {
            log.warn("Problems with write setting file: {} ", e.getMessage());
        }
    }

    public void resetFlags() {
        this.loginConfirm = false;
        this.loginDecline = false;
        this.registrationConfirm = false;
        this.registrationDecline = false;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }
}
