package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.AuthServerService;

import java.util.Optional;

@Slf4j
public class ServerAuthHandler extends SimpleChannelInboundHandler<Request<User>> {
    private AuthServerService authServerService;

    public void setAuthServerService(AuthServerService authServerService) {
        this.authServerService = authServerService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request<User> request) throws Exception {
        log.debug("received {}", request.getType().name());
        Response<UserTo> authResponse;

        switch (request.getType()) {
            case LOGIN:
                Optional<User> optionalUser = authServerService.loginRequest(request.getRequestBody());
                if (optionalUser.isPresent()) {
                    authResponse = new AuthResponse(AuthResponseType.LOGIN_OK, optionalUser.get());
                    ctx.writeAndFlush(authResponse);
                }
                break;

            case REGISTRATION:
                User user = request.getRequestBody();
                if (authServerService.findUserByUsername(user.getUsername())) {
                    authServerService.registerNewUser(user);

                    if(authServerService.findUserByUsername(user.getUsername())) {
                        authResponse = new AuthResponse(AuthResponseType.REGISTRATION_OK, user);
                        ctx.writeAndFlush(authResponse);
                        break;
                    }
                }

                ctx.writeAndFlush(new AuthResponse(AuthResponseType.REGISTRATION_WRONG_USER_EXIST));
                break;
        }
    }
}
