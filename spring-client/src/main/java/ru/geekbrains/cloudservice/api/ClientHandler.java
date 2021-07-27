package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;

@Slf4j
@ChannelHandler.Sharable
@Controller
public class ClientHandler extends SimpleChannelInboundHandler<ResponseMessage> {

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

    public void sendRequestToServer(RequestMessage requestMessage) {
        channelHandlerContext.channel().writeAndFlush(requestMessage);
//        channelHandlerContext.writeAndFlush(requestMessage).addListener((ChannelFutureListener) future -> log.info("channel future op complete"));

        log.info("sent {}", requestMessage.getRequest());
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
        ResponseMessage responseMessage = msg;
        Response response = responseMessage.getResponse();
        if (response instanceof AuthResponse) {
            authResponseHandler.processHandler(responseMessage);
        }

        if (response instanceof FileOperationResponse) {
            filesOperationResponseHandler.processHandler(responseMessage);
        }
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
