package ru.geekbrains.cloudservice.api;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponseType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

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
    private final FileInfoTo fileInfoTo;
    private final UserOperationalPathsRepo userOperationalPathsRepo;

    public ServerFileHandler(Path filePath, FileInfoTo fileInfoTo, UserOperationalPathsRepo userOperationalPathsRepo) {
        this.filePath = filePath;
        this.fileInfoTo = fileInfoTo;
        fileInfoTo.setLocalDateTime(LocalDateTime.now());
        this.userOperationalPathsRepo = userOperationalPathsRepo;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        log.info("file handler active");

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
        log.info("file handler read complete");
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

            if (Files.size(filePath) == fileInfoTo.getSize()) {
                userOperationalPathsRepo.saveFileInfo(fileInfoTo);
                channelReadComplete(ctx);
                ctx.pipeline().remove(this);
            }

        } catch (Exception e) {
            log.warn("Filehandler exception {}", e.getMessage());
            ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_SAVING_PROBLEM), fileInfoTo));
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
