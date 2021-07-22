package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.auth.Authenticator;

@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<Authenticator> {
    private ChannelHandlerContext channelHandlerContext;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connected to server");
        this.channelHandlerContext = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Authenticator command) throws Exception {
        switch (command.getType()) {
            case LOGIN_OK:

            case LOGIN_WRONG:

            case REGISTRATION_OK:

            case REGISTRATION_WRONG_USER_EXIST:
        }
    }

    public void sendLoginRequest(String username, String password) {
        Authenticator authenticator = new AuthRequest(username, password);
        channelHandlerContext.writeAndFlush(authenticator);
    }

}
