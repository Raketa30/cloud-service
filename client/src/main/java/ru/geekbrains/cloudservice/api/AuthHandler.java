package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.service.AuthService;

@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<Response<UserTo>> {
    private ChannelHandlerContext channelHandlerContext;
    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connected to server");
        this.channelHandlerContext = ctx;
    }

    public void sendLoginRequest(String username, String password) {
        channelHandlerContext.writeAndFlush(new AuthRequest(username, password, AuthRequestType.LOGIN));
    }

    public void sendRegistrationRequest(String username, String password) {
        channelHandlerContext.writeAndFlush(new AuthRequest(username, password, AuthRequestType.REGISTRATION));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response<UserTo> response) throws Exception {
        switch (response.getResponseType()) {
            case LOGIN_OK:
                authService.confirmLoginRequest(response.getResponseBody());
                break;
            case LOGIN_WRONG:
                break;
            case REGISTRATION_OK:
                break;
            case REGISTRATION_WRONG_USER_EXIST:
                break;
        }
    }
}
