package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
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

    public void processRequest(Request<User, AuthRequestType> request, ChannelHandlerContext ctx) {
        switch (request.getType()) {
            case LOGIN:
                Optional<User> optionalUser = authServerService.loginRequest(request.getRequestBody());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    ctx.writeAndFlush(new AuthResponse(AuthResponseType.LOGIN_OK, user));
                    activeUser = user;
                    break;
                }
                ctx.writeAndFlush(new AuthResponse(AuthResponseType.LOGIN_WRONG));
                break;

            case REGISTRATION:
                User user = request.getRequestBody();
                Optional<User> optionalUser1 = authServerService.findUserByUsername(user.getUsername());

                if (optionalUser1.isEmpty()) {
                    authServerService.registerNewUser(user);
                    ctx.writeAndFlush(new AuthResponse(AuthResponseType.REGISTRATION_OK, user));
                    log.info(user.toString());
                    break;
                }

                ctx.writeAndFlush(new AuthResponse(AuthResponseType.REGISTRATION_WRONG_USER_EXIST));
                break;

            case LOGOUT:
                authServerService.removeLoggedUser(ctx);
                ctx.close();
                break;
        }
    }


}
