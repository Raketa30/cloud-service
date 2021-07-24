package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.model.FileInfo;

@Slf4j
@Component
public class FileHandler extends SimpleChannelInboundHandler<FileInfo> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileInfo fileInfo) throws Exception {

    }
}
