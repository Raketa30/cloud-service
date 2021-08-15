package ru.geekbrains.cloudservice.service;

import io.netty.channel.DefaultFileRegion;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.api.ServerMessageHandler;
import ru.geekbrains.cloudservice.commands.impl.RequestMessage;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.files.*;
import ru.geekbrains.cloudservice.dto.FileTO;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
public class ServerFileService {
    private final ServerMessageHandler clientHandler;
    private final ServerFileOperationService operationService;

    public ServerFileService(ServerMessageHandler clientHandler, ServerFileOperationService operationService) {
        this.clientHandler = clientHandler;
        this.operationService = operationService;
    }

    public void saveFile(RequestMessage requestMessage) {
        FileTransferMessage message = (FileTransferMessage) requestMessage.getAbstractMessageObject();
        FileTO to = message.getFileTO();
        clientHandler.createFileHandler(operationService.getFullPath(to.getRelativePath()), to);
        sendUpdatedList(message.getParent());
    }

    public void saveDirectory(RequestMessage requestMessage) {
        FileTransferMessage message = (FileTransferMessage) requestMessage.getAbstractMessageObject();
        FileTO to = message.getFileTO();
        Path fullPath = operationService.getFullPath(to.getRelativePath());
        if (Files.notExists(fullPath)) {
            try {
                Files.createDirectories(fullPath);
            } catch (IOException e) {
                log.warn("Exception while create directories");
            }
        }
        sendUpdatedList(message.getParent());
    }

    public void getFileInfoListForView(RequestMessage requestMessage) {
        FilesListMessage message = (FilesListMessage) requestMessage.getAbstractMessageObject();
        sendUpdatedList(message.getParent());
    }

    public void folderUp(RequestMessage requestMessage) {
        FolderUpMessage upMessage = (FolderUpMessage) requestMessage.getAbstractMessageObject();
        String parent = operationService.getRelativeParentPath(upMessage.getCurrentPath());
        sendUpdatedList(parent);
    }

    private void sendUpdatedList(String parent) {
        Path fullPath = operationService.getFullPath(parent);
        if (Files.notExists(fullPath)) {
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_NOT_EXIST)));
        } else {
            FilesListMessage filesListMessage= new FilesListMessage(parent);
            filesListMessage.setFileTOS(operationService.getFileInfoTOList(fullPath));
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_LIST_SENT), filesListMessage));
        }
    }

    public void downloadFile(RequestMessage requestMessage) {
        FileTransferMessage message = (FileTransferMessage) requestMessage.getAbstractMessageObject();
        FileTO to = message.getFileTO();
        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_SENT), message));
        sendFileToClient(to, operationService.getFullPath(to.getRelativePath()));
    }

    public void downloadDirectory(RequestMessage requestMessage) {
        FileTransferMessage message = (FileTransferMessage) requestMessage.getAbstractMessageObject();
        FileTO to = message.getFileTO();
        Path fullPath = operationService.getFullPath(to.getRelativePath());
        if (Files.isDirectory(fullPath)) {
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.DIRECTORY_SENT), message));
            try {
                Files.walk(fullPath).forEach(p -> {
                    FileTO file = operationService.getFileTOFromFileInfo(new FileInfo(p));
                    FileTransferMessage transferMessage = new FileTransferMessage(operationService.getRelativePath(p).toString());
                    transferMessage.setFileTO(file);
                    if (Files.isDirectory(p)) {
                        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.DIRECTORY_SENT), transferMessage));
                    } else {
                        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_SENT), transferMessage));
                        sendFileToClient(file, p);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFileToClient(FileTO responseBody, Path filePath) {
        try {
            clientHandler.sendFileToClient(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }

    public void deleteFile(RequestMessage requestMessage) {
        FileTransferMessage message = (FileTransferMessage) requestMessage.getAbstractMessageObject();
        Path fullPath = operationService.getFullPath(message.getFileTO().getRelativePath());
        try {
            Files.walk(fullPath).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
