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
import ru.geekbrains.cloudservice.service.AuthServerService;

import java.util.Optional;

@Slf4j
public class ServerAuthHandler {
    private final AuthServerService authServerService;
    @Getter
    private User activeUser;

    public ServerAuthHandler(AuthServerService authServerService) {
        this.authServerService = authServerService;
    }

    public void processRequest(RequestMessage requestMessage, ChannelHandlerContext ctx) {
        AuthRequestType requestType = (AuthRequestType) requestMessage.getRequest().getRequestCommandType();

        switch (requestType) {
            case LOGIN:
                User tempUser = (User) requestMessage.getAbstractMessageObject();
                Optional<User> optionalUser = authServerService.loginRequest(tempUser);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    ctx.write(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_OK), new UserTo(user.getUsername())));
                    ctx.flush();
                    activeUser = user;
                    log.info("user {} successfully logged", user);
                    break;
                }
                ctx.writeAndFlush(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_WRONG)));
                log.info("wrong login attempt");
                break;

            case REGISTRATION:
                User user = (User) requestMessage.getAbstractMessageObject();
                Optional<User> optionalUser1 = authServerService.findUserByUsername(user.getUsername());

                if (optionalUser1.isEmpty()) {
                    authServerService.registerNewUser(user);
                    ctx.writeAndFlush(new ResponseMessage(new AuthResponse(AuthResponseType.REGISTRATION_OK), new UserTo(user.getUsername())));
                    log.info(user.toString());
                    break;
                }

                ctx.channel().writeAndFlush(new AuthResponse(AuthResponseType.REGISTRATION_WRONG_USER_EXIST));
                log.info("wrong registration attempt, user exist");
                break;

            case LOGOUT:
                authServerService.removeLoggedUser(ctx);
                ctx.close();
                break;
        }
    }


}
