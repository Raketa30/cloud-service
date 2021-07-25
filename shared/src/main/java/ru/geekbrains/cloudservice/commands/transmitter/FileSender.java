package ru.geekbrains.cloudservice.commands.transmitter;

import io.netty.channel.ChannelHandlerContext;
import ru.geekbrains.cloudservice.commands.Constants;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class FileSender {

    private final ChannelHandlerContext handlerContext;

    public FileSender(ChannelHandlerContext handlerContext) {
        this.handlerContext = handlerContext;
    }

    public void transfer(FileChannel fileChannel, long position, long size) throws IOException {
        while(position < size) {
            position += fileChannel.transferTo(position, Constants.TRANSFER_MAX_SIZE, (SocketChannel)this.handlerContext.channel());
        }
    }

}
