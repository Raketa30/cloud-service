package ru.geekbrains.cloudservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientMessageHandler;
import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.commands.impl.RequestMessage;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthRequestType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.model.User;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
public class ClientAuthService {
    private final ClientMessageHandler clientMessageHandler;
    private final ClientFilesOperationService operationService;
    private final DataModel dataModel;

    @Autowired
    public ClientAuthService(ClientMessageHandler clientMessageHandler, ClientFilesOperationService operationService, DataModel dataModel) {
        this.clientMessageHandler = clientMessageHandler;
        this.operationService = operationService;
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
            dataModel.setRootPath("*empty");
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

    public Optional<String> findUserFolderPath(UserTo user) {
        String username = user.getUsername();
        String folder = operationService.getUserFolder(username);
        if (folder.equals("")) {
            return Optional.empty();
        }
        return Optional.of(folder);
    }

    public void createLocalUserDirectory(String pickedDirectory, String username) {
        Path path = Paths.get(pickedDirectory).resolve("GeekbrainsCloud-" + username);
        operationService.createUserFolder(path, username);
    }

    public void createAndSetUserDirectory(String pickedDirectory) {
        String loggedUser = dataModel.getUser().getUsername();
        Path path = Paths.get(pickedDirectory).resolve("GeekbrainsCloud-" + loggedUser);
        operationService.createUserFolder(path, loggedUser);
        dataModel.setRootPath(operationService.getUserFolder(loggedUser));
    }
}
