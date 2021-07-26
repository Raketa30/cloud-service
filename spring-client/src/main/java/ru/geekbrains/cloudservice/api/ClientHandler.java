package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;

@Slf4j
@ChannelHandler.Sharable
@Service
public class ClientHandler extends SimpleChannelInboundHandler<Response<?,?>> {
    @Autowired
    private AuthResponseHandler authResponseHandler;
    @Autowired
    private FilesOperationResponseHandler filesOperationResponseHandler;

    private ChannelHandlerContext channelHandlerContext;

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

    public void sendRequestToServer(Request<?, ?> request) {
        channelHandlerContext.writeAndFlush(request);
        log.info("sent {}", request.getType());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        if (response.getResponseType() instanceof AuthResponseType) {
            authResponseHandler.processHandler(response);
        }

        if (response.getResponseType() instanceof FileOperationResponse) {
            filesOperationResponseHandler.processHandler(response);
        }

    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
