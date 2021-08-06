package ru.geekbrains.cloudservice.api;

import io.netty.channel.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.geekbrains.cloudservice.commands.Message;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.dto.FileInfoTo;

import java.nio.file.Path;

@Slf4j
@Controller
//@ChannelHandler.Sharable
public class ClientMessageHandler extends SimpleChannelInboundHandler<Message> {

    @Autowired
    private ClientAuthHandler clientAuthHandler;

    @Autowired
    private FilesOperationResponseHandler filesOperationResponseHandler;

    private ChannelHandlerContext channelHandlerContext;

    @Getter
    @Setter
    private boolean isReady;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        ResponseMessage responseMessage = (ResponseMessage) msg;
        Response response = responseMessage.getResponse();
        if (response instanceof AuthResponse) {
            clientAuthHandler.processHandler(responseMessage);
        }

        if (response instanceof FileOperationResponse) {
            filesOperationResponseHandler.processHandler(responseMessage);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("Clienthandler registered {}", ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.info("Clienthandler unregistered {}", ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelHandlerContext = ctx;
        log.info("Clienthandler active {}", ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("Clienthandler inactive {}", ctx);
    }

    public void sendRequestToServer(Message requestMessage) {
        isReady = false;
        ChannelFuture sendFileFuture = channelHandlerContext.writeAndFlush(requestMessage, channelHandlerContext.newProgressivePromise());
        addListener(sendFileFuture);
        log.info("sent {}", requestMessage);
    }

    public void sendFileToServer(DefaultFileRegion defaultFileRegion) {
        isReady = false;
        ChannelFuture sendFileFuture = channelHandlerContext.writeAndFlush(defaultFileRegion, channelHandlerContext.newProgressivePromise());
        addListener(sendFileFuture);
    }

    private void addListener(ChannelFuture sendFileFuture) {
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
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

    public void createFileHandler(Path filePath, FileInfoTo fileInfoTo) {
        ClientFileHandler clientFileHandler = new ClientFileHandler(filePath, fileInfoTo);

        ChannelPipeline pipeline = channelHandlerContext.pipeline()
                .addBefore("od", "fh", clientFileHandler);
        log.info(pipeline.toString());
        try {
            clientFileHandler.channelRegistered(channelHandlerContext);
            clientFileHandler.channelActive(channelHandlerContext);
        } catch (Exception e) {
            log.debug("server File Handler register error: {}", e.getMessage());
        }
        log.debug(pipeline.toString());
    }
}
