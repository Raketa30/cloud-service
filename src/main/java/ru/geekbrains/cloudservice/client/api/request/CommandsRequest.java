package ru.geekbrains.cloudservice.client.api.request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.client.service.commands.AbstractCommand;

@Slf4j
public class CommandsRequest extends SimpleChannelInboundHandler<AbstractCommand> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand msg) throws Exception {
    }
}
