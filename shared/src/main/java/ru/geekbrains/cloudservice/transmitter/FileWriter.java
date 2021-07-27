package ru.geekbrains.cloudservice.transmitter;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileWriter {
    private final FileChannel fileChannel;

    public FileWriter(Path path) throws IOException {
        this.fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    }

    public void transfer(ChannelHandlerContext channelHandlerContext, long bytes) throws IOException {
        long position = 0L;

        while(position < bytes) {
//            position += this.fileChannel.transferFrom(channelHandlerContext.alloc().buffer(), Constants.TRANSFER_MAX_SIZE);
        }
    }
}
