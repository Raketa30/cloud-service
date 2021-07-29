package ru.geekbrains.cloudservice.api;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ServerFileHandler extends ChunkedWriteHandler {
    private final Path filePath;
    private final FileInfo fileInfo;

    public ServerFileHandler(Path filePath, FileInfo fileInfo) {
        this.filePath = filePath;
        this.fileInfo = fileInfo;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        log.info("file handler active");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            File file = filePath.toFile();//remember to change dest
            if (!file.exists()) {
                file.createNewFile();
            }

            ByteBuf byteBuf = (ByteBuf) msg;
            ByteBuffer byteBuffer = byteBuf.nioBuffer();
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
                FileChannel fileChannel = randomAccessFile.getChannel();

                while (byteBuffer.hasRemaining()) {
                    fileChannel.position(file.length());
                    fileChannel.write(byteBuffer);
                }

                byteBuf.release();
                fileChannel.close();
            }

            if (Files.size(filePath) == fileInfo.getSize()) {
                ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_SAVED), fileInfo));
                ctx.pipeline().remove(this);
            }

        } catch (Exception e) {
            log.warn("Filehandler exception");
            ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_SAVING_PROBLEM), fileInfo));
            Files.delete(filePath);
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("filehjandler exception");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.pipeline().remove(this);
//        System.out.println("channel read complete");
//        log.warn(ctx.pipeline().toString());
//    }
}
