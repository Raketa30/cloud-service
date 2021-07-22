package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.auth.Authenticator;
import ru.geekbrains.cloudservice.service.AuthServerService;

@Slf4j
public class ServerAuthHandler extends SimpleChannelInboundHandler<Authenticator> {
    private AuthServerService authServerService;

    public void setAuthServerService(AuthServerService authServerService) {
        this.authServerService = authServerService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Authenticator authenticator) throws Exception {
        log.debug("received {}", authenticator.getType().name());

        switch (authenticator.getType()) {
            case LOGIN:
                authServerService.loginRequest(authenticator.getUserFromRequest());
            case REGISTRATION:
        }
    }
}
