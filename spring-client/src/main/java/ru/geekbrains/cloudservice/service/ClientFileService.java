package ru.geekbrains.cloudservice.service;

import io.netty.channel.DefaultFileRegion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientHandler;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequest;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ClientFileService {
    private final ClientHandler clientHandler;
    private final ClientAuthService clientAuthService;

    @Autowired
    public ClientFileService(ClientHandler clientHandler, ClientAuthService clientAuthService) {
        this.clientHandler = clientHandler;
        this.clientAuthService = clientAuthService;
    }

    public void sendRequestForFileSaving(FileInfo localFileInfo) {
        FileInfoTo fileInfo = getFileInfoTo(localFileInfo);
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), fileInfo));
    }

    public void sendFileToServer(ResponseMessage responseMessage) {
        FileInfoTo responseBody = (FileInfoTo) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(responseBody);
        try {
            if (!Files.isHidden(filePath) && Files.isReadable(filePath)) {
                clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), responseBody));
                sendFileToServer(responseBody, filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFileToServer(FileInfoTo responseBody, Path filePath) {
        try {
            clientHandler.sendFileToServer(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }

    public void sendDirectoryToServer(ResponseMessage responseMessage) {
        FileInfoTo responseBody = (FileInfoTo) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(responseBody);
        FileInfo fileInfo = new FileInfo(filePath);
        fileInfo.setRelativePath(clientAuthService.getUserFolderPath().relativize(filePath));
        FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), fileInfoTo));
    }

    public void sendRequestForFileDownloading(FileInfo fileInfo) {
        FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DOWNLOAD_FILE), fileInfoTo));
    }

    public void sendRequestForDeleting(FileInfo fileInfo) {
    }


    private Path getFilePath(FileInfoTo responseBody) {
        return clientAuthService.getUserFolderPath().resolve(responseBody.getFilePath());
    }

    private FileInfoTo getFileInfoTo(FileInfo localFileInfo) {
        String fileName = localFileInfo.getFilename();
        String relativePath = localFileInfo.getRelativePath().toString();
        String fileType = localFileInfo.getFileType();
        Long fileSize = localFileInfo.getFileSize();
        LocalDateTime dateModified = localFileInfo.getLastModified();

        FileInfoTo fileInfo = new FileInfoTo(fileName, relativePath, fileType, fileSize, dateModified);

        Path parentPath = Paths.get(relativePath).getParent();

        if (parentPath == null) {
            fileInfo.setParentPath("root");
        } else {
            fileInfo.setParentPath(parentPath.toString());
        }
        return fileInfo;
    }

}


