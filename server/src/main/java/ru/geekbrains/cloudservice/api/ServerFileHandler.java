package ru.geekbrains.cloudservice.api;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.impl.files.FileOperationResponseType;
import ru.geekbrains.cloudservice.dto.FileTO;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Slf4j
public class ServerFileHandler extends ChunkedWriteHandler {
    private final Path filePath;
    private final FileTO fileTO;

    public ServerFileHandler(Path filePath, FileTO fileTO) {
        this.filePath = filePath;
        this.fileTO = fileTO;
        fileTO.setLastMod(LocalDateTime.now());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        log.info("file handler active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            File file = filePath.toFile();

            if (!file.exists()) {
                Files.createDirectories(filePath.getParent());
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

            if (Files.size(filePath) == fileTO.getSize()) {
                ctx.pipeline().remove(this);
            }

        } catch (Exception e) {
            log.warn("Filehandler exception {}", e.getMessage());
            ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_SAVING_PROBLEM), fileTO));
            Files.delete(filePath);
            channelInactive(ctx);
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("filehandler exception");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }
}
