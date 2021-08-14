package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.ServerAuthService;

import java.util.Optional;

@Slf4j
public class ServerAuthHandler {
    private final ServerAuthService serverAuthService;
    private final ServerMessageHandler clientHandler;
    @Getter
    private User activeUser;

    public ServerAuthHandler(ServerAuthService serverAuthService, ServerMessageHandler clientHandler) {
        this.serverAuthService = serverAuthService;
        this.clientHandler = clientHandler;
    }

    public void processRequest(RequestMessage requestMessage, ChannelHandlerContext ctx) {
        AuthRequestType requestType = (AuthRequestType) requestMessage.getRequest().getRequestCommandType();

        switch (requestType) {
            case LOGIN:
                User tempUser = (User) requestMessage.getAbstractMessageObject();
                Optional<User> optionalUser = serverAuthService.loginRequest(tempUser);
                if (optionalUser.isPresent()) {
                    User logUser = optionalUser.get();
                    clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_OK), new UserTo(logUser.getUsername())));
                    activeUser = logUser;
                    log.info("logUser {} successfully logged", logUser);
                    break;
                }
                clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_WRONG)));
                log.info("wrong login attempt");
                break;

            case REGISTRATION:
                User user = (User) requestMessage.getAbstractMessageObject();
                Optional<User> regUser = serverAuthService.findUserByUsername(user.getUsername());

                if (regUser.isEmpty()) {
                    serverAuthService.registerNewUser(user);
                    clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.REGISTRATION_OK), new UserTo(user.getUsername())));
                    log.info(user.toString());
                    break;
                }

                clientHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.REGISTRATION_WRONG_USER_EXIST)));
                log.info("wrong registration attempt, user exist");
                break;

            case LOGOUT:
                serverAuthService.removeLoggedUser(ctx);
                ctx.close();
                break;
        }
    }


}
