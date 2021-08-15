package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.impl.RequestMessage;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthRequestType;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthResponseType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.ServerAuthService;

import java.util.Optional;

@Slf4j

public class ServerAuthHandler {
    private final ServerMessageHandler clientHandler;
    private final ServerAuthService authService;


    public ServerAuthHandler(ServerMessageHandler clientHandler, ServerAuthService authService) {
        this.clientHandler = clientHandler;
        this.authService = authService;
    }

    public void processRequest(RequestMessage requestMessage) {
        AuthRequestType requestType = (AuthRequestType) requestMessage.getRequest().getRequestCommandType();

        switch (requestType) {
            case LOGIN:
                Optional<User> loginUser = authService.loginUser(requestMessage);
                if (loginUser.isPresent()) {
                    clientHandler.setUser(loginUser.get());
                    clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_OK),new UserTo(loginUser.get().getUsername())));
                } else {
                    clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_WRONG)));
                }
                break;

            case REGISTRATION:
                Optional<User> regUser = authService.registerUser(requestMessage);
                if (regUser.isPresent()) {
                    clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.REGISTRATION_OK), new UserTo(regUser.get().getUsername())));
                } else {
                    clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.REGISTRATION_WRONG_USER_EXIST)));
                }
                break;

            case LOGOUT:
                authService.removeLoggedUser();
                break;
        }
    }

    public ServerAuthService getAuthService() {
        return authService;
    }
}
