package ru.geekbrains.cloudservice.api;

import io.netty.channel.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Message;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.impl.RequestMessage;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.impl.files.FileOperationRequest;
import ru.geekbrains.cloudservice.dto.FileTO;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.ServerAuthService;

import java.nio.file.Path;

@Slf4j
@ChannelHandler.Sharable
public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {
    private final ServerAuthHandler serverAuthHandler;
    private final ServerFilesOperationHandler serverFilesOperationHandler;

    @Getter
    private ChannelHandlerContext channelHandlerContext;
    @Getter
    @Setter
    private boolean isReady;

    public ServerMessageHandler(ServerAuthService authService) {
        serverAuthHandler = new ServerAuthHandler(this, authService);
        serverFilesOperationHandler = new ServerFilesOperationHandler(this);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("Serverclienthandler registered {}", ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channelHandlerContext = ctx;
        log.info("Serverclienthandler active {}", ctx);
    }

    public void sendResponse(ResponseMessage response) {
        isReady = false;
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(response, channelHandlerContext.newProgressivePromise());
        addListener(channelFuture);
    }

    private void addListener(ChannelFuture channelFuture) {
        channelFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    log.info(future.channel() + " Transfer progress: " + progress);
                } else {
                    log.info(future.channel() + " Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                log.info(future.channel() + " Transfer complete.");
                isReady = true;
            }
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.info("Server client handler unregistered {}", ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        RequestMessage requestMessage = (RequestMessage) msg;
        Request request = requestMessage.getRequest();

        if (request instanceof AuthRequest) {
            serverAuthHandler.processRequest(requestMessage);
        }
        if (request instanceof FileOperationRequest) {
            serverFilesOperationHandler.processRequest(requestMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireChannelActive();
        cause.printStackTrace();
    }

    public void createFileHandler(Path fullPath, FileTO fileTO) {
        ServerFileHandler serverFileHandler = new ServerFileHandler(fullPath, fileTO);
        ChannelPipeline pipeline = channelHandlerContext.pipeline()
                .addBefore("od", "fh", serverFileHandler);
        log.info(pipeline.toString());

        try {
            serverFileHandler.channelRegistered(channelHandlerContext);
            serverFileHandler.channelActive(channelHandlerContext);
        } catch (Exception e) {
            log.info("server File Handler register error: {}", e.getMessage());
        }
        log.info(pipeline.toString());
    }

    public void sendFileToClient(DefaultFileRegion defaultFileRegion) {
        isReady = false;
        ChannelFuture sendFileFuture = channelHandlerContext.writeAndFlush(defaultFileRegion, channelHandlerContext.newProgressivePromise());
        addListener(sendFileFuture);
    }

    public void setUser(User user) {
        serverFilesOperationHandler.setServerFileService(user);
    }
}
