package ru.geekbrains.cloudservice.transmitter;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class FileReceiver {
    private ChannelHandlerContext channelHandlerContext;
    private FileWriter fileWriter;
    private long size;

    public FileReceiver(ChannelHandlerContext channelHandlerContext, FileWriter fileWriter, long size) {
        this.channelHandlerContext = channelHandlerContext;
        this.fileWriter = fileWriter;
        this.size = size;
    }

    public void receive() throws IOException {
        doTransfer();
    }

    private void doTransfer() throws IOException {
        this.fileWriter.transfer(channelHandlerContext, this.size);
    }
}
