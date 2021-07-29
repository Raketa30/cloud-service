package ru.geekbrains.cloudservice.service;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientHandler;
import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequest;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.LocalFileInfo;

import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@Service
public class FileService {
    private final ClientHandler clientHandler;
    private final AuthService authService;

    @Autowired
    public FileService(ClientHandler clientHandler, AuthService authService) {
        this.clientHandler = clientHandler;
        this.authService = authService;
    }

    public void sendRequestForFileSaving(LocalFileInfo localFileInfo) {
        AbstractMessage fileInfo = new FileInfo(localFileInfo.getRelativePath().toString(), localFileInfo.getFileType(), localFileInfo.getFileSize());
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), fileInfo));
        log.info("sendRequestForFileSaving {}", localFileInfo);
    }

    public void sendFileToServer(FileInfo responseBody) {
        Path filePath = authService.getUserFolderPath().resolve(responseBody.getFilePath());
        try {
            ChannelHandlerContext context = clientHandler.getChannelHandlerContext();
            context.write(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), responseBody));

            ChannelFuture sendFileFuture;
            sendFileFuture = context.writeAndFlush(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()),
            context.newProgressivePromise());
            // Write the end marker.

            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                    if (total < 0) { // total unknown
                        log.debug(future.channel() + " Transfer progress: " + progress);
                    } else {
                        log.debug(future.channel() + " Transfer progress: " + progress + " / " + total);
                    }
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future) {
                    log.debug(future.channel() + " Transfer complete.");
                    context.fireChannelActive();
                }

                });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
