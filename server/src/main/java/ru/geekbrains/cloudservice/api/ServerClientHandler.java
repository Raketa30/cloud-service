package ru.geekbrains.cloudservice.api;

import io.netty.channel.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Message;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthRequest;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequest;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;
import ru.geekbrains.cloudservice.service.AuthServerService;

import java.nio.file.Path;

@Slf4j
public class ServerClientHandler extends SimpleChannelInboundHandler<Message> {
    private final ServerAuthHandler serverAuthHandler;
    private final ServerFilesOperationHandler serverFilesOperationHandler;
    @Getter
    private ChannelHandlerContext channelHandlerContext;
    @Getter
    @Setter
    private boolean isReady;

    public ServerClientHandler(AuthServerService authServerService) {
        serverAuthHandler = new ServerAuthHandler(authServerService, this);
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
                    log.debug(future.channel() + " Transfer progress: " + progress);
                } else {
                    log.debug(future.channel() + " Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                log.debug(future.channel() + " Transfer complete.");
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
            serverAuthHandler.processRequest(requestMessage, ctx);
        }
        if (request instanceof FileOperationRequest) {
            serverFilesOperationHandler.processRequest(requestMessage, serverAuthHandler.getActiveUser());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireChannelActive();
        log.warn(cause.getMessage());
    }

    public void createFileHandler(Path fullPath, FileInfoTo fileInfoTo, UserOperationalPathsRepo userOperationalPathsRepo) {
        ServerFileHandler serverFileHandler = new ServerFileHandler(fullPath, fileInfoTo, userOperationalPathsRepo);
        ChannelPipeline pipeline = channelHandlerContext.pipeline()
                .addBefore("od", "fh", serverFileHandler);
        log.info(pipeline.toString());

        try {
            serverFileHandler.channelRegistered(channelHandlerContext);
            serverFileHandler.channelActive(channelHandlerContext);
        } catch (Exception e) {
            log.debug("server File Handler register error: {}", e.getMessage());
        }
        log.debug(pipeline.toString());
    }

    public void sendFileToServer(DefaultFileRegion defaultFileRegion) {
        isReady = false;
        ChannelFuture sendFileFuture = channelHandlerContext.writeAndFlush(defaultFileRegion, channelHandlerContext.newProgressivePromise());
        addListener(sendFileFuture);
    }
}
