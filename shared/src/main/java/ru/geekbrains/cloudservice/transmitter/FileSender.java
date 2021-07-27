package ru.geekbrains.cloudservice.transmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileSender {

    private final ChannelHandlerContext channelHandlerContext;

    public FileSender(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public void transfer(FileChannel fileChannel, long position, long size) throws IOException {

        FileRegion fileRegion = new DefaultFileRegion(fileChannel, position, size);

        while(position < size) {
            position += fileRegion.transferTo(fileChannel, position);
        }
        channelHandlerContext.writeAndFlush(fileRegion);
    }

}
