package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.service.AuthServerService;

@Slf4j
public class ServerClientHandler extends SimpleChannelInboundHandler<Request<?, ?>> {
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
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {

        if (request.getType() instanceof AuthRequestType) {
            serverAuthHandler.processRequest(request, ctx);
        }
        if (request.getType() instanceof FileOperationRequestType) {
            if (serverAuthHandler.getActiveUser() != null) {
                log.warn("user not loggined for file operations");
            } else {
                serverFilesOperationHandler.setActiveUser(serverAuthHandler.getActiveUser());
                serverFilesOperationHandler.processRequest(request, ctx);
            }
        }


    }
}
