package ru.geekbrains.cloudservice.transmitter;

import io.netty.channel.*;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.nio.channels.FileChannel;

public class FileSender {

    private final ChannelHandlerContext context;

    public FileSender(ChannelHandlerContext context) {
        this.context = context;
    }

    public void transfer(FileInfo fileInfo, FileChannel fileChannel, long position, long size){
        // Write the content.
        ChannelFuture sendFileFuture;

        sendFileFuture = context.writeAndFlush(new DefaultFileRegion(fileChannel, position, size),
                context.newProgressivePromise());

        // Write the end marker.

        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    System.err.println(future.channel() + " Transfer progress: " + progress);
                } else {
                    System.err.println(future.channel() + " Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                System.err.println(future.channel() + " Transfer complete.");
                context.writeAndFlush(new FileOperationResponse(FilesOperationResponseType.FILE_SENT));
            }
        });
    }

}
