package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Message;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequest;
import ru.geekbrains.cloudservice.service.AuthServerService;

@Slf4j
@ChannelHandler.Sharable
public class ServerClientHandler extends SimpleChannelInboundHandler<Message> {
    private final ServerAuthHandler serverAuthHandler;
    private ServerFilesOperationHandler serverFilesOperationHandler;

    public ServerClientHandler(AuthServerService authServerService) {
        serverAuthHandler = new ServerAuthHandler(authServerService);
        serverFilesOperationHandler = new ServerFilesOperationHandler();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("Serverclienthandler registered {}", ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Serverclienthandler active {}", ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.info("Serverclienthandler unregistered {}", ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        RequestMessage requestMessage = (RequestMessage) msg;
        Request request = requestMessage.getRequest();

        if (request instanceof AuthRequest) {
            serverAuthHandler.processRequest(requestMessage, ctx);
        }
        if (request instanceof FileOperationRequest) {
            serverFilesOperationHandler.processRequest(requestMessage, ctx, serverAuthHandler.getActiveUser());
        }
    }

}
