package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.AuthServerService;

import java.util.Optional;

@Slf4j
@ChannelHandler.Sharable
public class ServerAuthHandler extends SimpleChannelInboundHandler<Request<User, AuthRequestType>> {
    private final AuthServerService authServerService;

    public ServerAuthHandler(AuthServerService authServerService) {
        this.authServerService = authServerService;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("client connected");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channel read complete");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request<User, AuthRequestType> request) throws Exception {
        log.debug("received {}", request.getType().name());
        Response<UserTo> authResponse;

        switch (request.getType()) {

            case LOGIN:
                Optional<User> optionalUser = authServerService.loginRequest(request.getRequestBody());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    authResponse = new AuthResponse(AuthResponseType.LOGIN_OK, user);
                    authServerService.addLoggedUser(ctx, user);
                    ctx.writeAndFlush(authResponse);
                }

                authResponse = new AuthResponse(AuthResponseType.LOGIN_WRONG);
                ctx.writeAndFlush(authResponse);
                break;

            case REGISTRATION:
                User user = request.getRequestBody();
                Optional<User> optionalUser1 = authServerService.findUserByUsername(user.getUsername());

                if (optionalUser1.isEmpty()) {
                    authServerService.registerNewUser(user);
                    authResponse = new AuthResponse(AuthResponseType.REGISTRATION_OK, user);
                    ctx.writeAndFlush(authResponse);
                    log.info(user.toString());
                    break;
                }

                ctx.writeAndFlush(new AuthResponse(AuthResponseType.REGISTRATION_WRONG_USER_EXIST));
                break;

            case LOGOUT:
                authServerService.removeLoggedUser(ctx);
                ctx.close();
        }
    }
}
