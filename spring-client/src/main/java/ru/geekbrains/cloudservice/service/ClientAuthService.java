package ru.geekbrains.cloudservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientMessageHandler;
import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
@Service
public class ClientAuthService {
    private final ClientMessageHandler clientMessageHandler;
    private final DataModel dataModel;

    @Autowired
    public ClientAuthService(ClientMessageHandler clientMessageHandler, DataModel dataModel) {
        this.clientMessageHandler = clientMessageHandler;
        this.dataModel = dataModel;
    }

    public void userLogin(String username, String password) {
        AbstractMessage abstractMessage = new User(username, password);
        clientMessageHandler.sendRequestToServer(new RequestMessage(new AuthRequest(AuthRequestType.LOGIN), abstractMessage));
    }

    public void registerUser(String username, String password) {
        AbstractMessage abstractMessage = new User(username, password);
        clientMessageHandler.sendRequestToServer(new RequestMessage(new AuthRequest(AuthRequestType.REGISTRATION), abstractMessage));
    }

    public void confirmLoginRequest(ResponseMessage message) {
        UserTo user = (UserTo) message.getAbstractMessageObject();
        Optional<String> optionalPath = findUserFolderPath(user);
        if (optionalPath.isPresent()) {
            String userFolder = optionalPath.get();
            dataModel.setRootPath(userFolder);
            log.info("logged");
        } else {
            dataModel.setRootPath("empty");
        }
        dataModel.setUser(user);
    }

    public void confirmRegistration(ResponseMessage message) {
        UserTo userTo = (UserTo) message.getAbstractMessageObject();
        log.info("Registered new user: {}", userTo.getUsername());
        dataModel.setRegisteredUser(userTo);
    }

    public void declineRegistration() {
        dataModel.setRegisteredUser(new UserTo("*empty"));
    }

    public void declineLoginRequest() {
        dataModel.setUser(new UserTo("*empty"));
        log.info("login wrong response");
    }

    //ищем папку пользователя в файле настроек
    public Optional<String> findUserFolderPath(UserTo user) {
        if (user == null) {
            return Optional.empty();
        }
        try (Scanner scanner = new Scanner(new File("spring-client/settings.txt"))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] credentials = line.split(" : ");

                if (credentials[0].equals(user.getUsername())) {
                    return Optional.of(credentials[1]);
                }
            }
        } catch (IOException e) {
            log.warn("File setting not found");
        }
        return Optional.empty();
    }

    //создаем папку пользователя при успешной регистрации
    public void createLocalUserDirectory(String pickedDirectory, String username) {
        String path = pickedDirectory + "/GeekbrainsCloud-" + username;
        if (Files.exists(Paths.get(path))) {
            return;
        }

        try {
            Files.createDirectory(Paths.get(path));

            File file = new File("spring-client/settings.txt");
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.write(username + " : " + path + "\n");
            }

        } catch (IOException e) {
            log.warn("Problems with write setting file: {} ", e.getMessage());
        }
        dataModel.setRootPath(path);
    }

}
