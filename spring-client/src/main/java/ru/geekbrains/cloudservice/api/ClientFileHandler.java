package ru.geekbrains.cloudservice.api;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.dto.FileInfoTo;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ClientFileHandler extends ChunkedWriteHandler {
    private final Path filePath;
    private final FileInfoTo fileInfoTo;

    public ClientFileHandler(Path filePath, FileInfoTo fileInfoTo) {
        this.filePath = filePath;
        this.fileInfoTo = fileInfoTo;
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

            if (Files.size(filePath) == fileInfoTo.getSize()) {
                log.warn("file received from server {}", fileInfoTo);
                ctx.pipeline().remove(this);
            }

        } catch (Exception e) {
            log.warn("Filehandler exception");
            log.warn("Problem with file receiving");
            Files.delete(filePath);
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("file handler exception");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }
}