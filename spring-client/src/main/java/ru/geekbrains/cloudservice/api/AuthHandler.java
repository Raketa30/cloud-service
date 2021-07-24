package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.service.AuthService;

@Slf4j
@Component
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<Response<UserTo>> {

    @Autowired
    private AuthService authService;

    private ChannelHandlerContext channelHandlerContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Connected to server");
        this.channelHandlerContext = ctx;
    }

    public void sendLoginRequest(String username, String password) {
        channelHandlerContext.writeAndFlush(new AuthRequest(username, password, AuthRequestType.LOGIN));
        log.info("sended login request");
    }

    public void sendRegistrationRequest(String username, String password) {
        channelHandlerContext.writeAndFlush(new AuthRequest(username, password, AuthRequestType.REGISTRATION));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response<UserTo> response) throws Exception {
        switch (response.getResponseType()) {
            case LOGIN_OK:
                /*
                * Отправляем в сервис тело трансферобжекта
                 * */
                authService.confirmLoginRequest(response.getResponseBody());
                log.info("recieved {}", response.getResponseBody().toString());
                break;

            case LOGIN_WRONG:
                authService.declineLoginRequest();
                break;

            case REGISTRATION_OK:
                authService.confirmRegistration(response.getResponseBody() );
                String username = response.getResponseBody().getUsername();
                log.info("Registered new user: {}", username);
                break;

            case REGISTRATION_WRONG_USER_EXIST:
                authService.declineRegistration();
                break;
        }
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
