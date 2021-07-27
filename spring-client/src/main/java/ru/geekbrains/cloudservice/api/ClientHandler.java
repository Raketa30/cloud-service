package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.geekbrains.cloudservice.commands.Message;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;

@Slf4j
@Controller
public class ClientHandler extends SimpleChannelInboundHandler<Message> {

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

    public void sendRequestToServer(Message requestMessage) {
//        channelHandlerContext.writeAndFlush(requestMessage);
        channelHandlerContext.writeAndFlush(requestMessage).addListener((ChannelFutureListener) future -> log.info("channel future op complete"));

        log.info("sent {}", requestMessage);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        ResponseMessage responseMessage = (ResponseMessage) msg;
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
