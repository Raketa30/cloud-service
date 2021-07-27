package ru.geekbrains.cloudservice.service;

import io.netty.handler.stream.ChunkedFile;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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

    public void sendFileToServer(FileInfo responseBody){
        Path filePath = authService.getUserFolderPath().resolve(responseBody.getFilePath());
        try {
            ChunkedFile chunkedFile = new ChunkedFile(new File(filePath.toString()));
            clientHandler.getChannelHandlerContext().writeAndFlush(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), responseBody));
            clientHandler.getChannelHandlerContext().writeAndFlush(chunkedFile);

        } catch (IOException e) {
            log.warn("file not found");
        }


    }
}
