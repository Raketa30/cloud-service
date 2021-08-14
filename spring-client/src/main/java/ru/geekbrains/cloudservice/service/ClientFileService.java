package ru.geekbrains.cloudservice.service;

import io.netty.channel.DefaultFileRegion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientMessageHandler;
import ru.geekbrains.cloudservice.commands.FilesListMessage;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequest;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.UploadedStatus;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ClientFileService {
    private final ClientMessageHandler messageHandler;
    private final ClientFilesOperationService operationService;
    private final DataModel dataModel;

    @Autowired
    public ClientFileService(ClientMessageHandler messageHandler, ClientFilesOperationService operationService, DataModel dataModel) {
        this.messageHandler = messageHandler;
        this.operationService = operationService;
        this.dataModel = dataModel;
    }

    public void sendFileToServer(FileInfo fileInfo) {
        Path filePath = fileInfo.getPath();
        if (Files.exists(filePath)) {
            if (Files.isDirectory(filePath)) {
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), fileInfo));
                try {
                    Files.walk(filePath).forEach(p -> {
                        FileInfo file = new FileInfo(p);
                        if (Files.isDirectory(p)) {
                            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), file));
                        } else {
                            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), file));
                            sendFileToServer(p, file);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), fileInfo));
                sendFileToServer(filePath, fileInfo);
            }
        }
    }

    private void sendFileToServer(Path filePath, FileInfo fileInfo) {
        try {
            messageHandler.sendFileToServer(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, fileInfo.getFileSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }

    public void sendRequestForFileDownloading(FileInfo fileInfo) {
        FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
        if (fileInfo.getFileType().equals("folder")) {
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DOWNLOAD_DIRECTORY), fileInfoTo));
        } else {
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DOWNLOAD_FILE), fileInfoTo));
        }
    }

    public void sendRequestForDeleting(FileInfo fileInfo) {
        FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
        messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DELETE_FILE), fileInfoTo));
    }

    private Path getFilePath(FileInfoTo to) {
        Path root = Paths.get(dataModel.getRootPath());
        return root.resolve(to.getFilePath());
    }

    private FileInfoTo getFileInfoTo(FileInfo localFileInfo) {
        String fileName = localFileInfo.getFilename();
        String filePath = localFileInfo.getRelativePath() == null ?
                Paths.get(dataModel.getRootPath()).relativize(localFileInfo.getPath()).toString() :
                localFileInfo.getRelativePath().toString();
        String fileType = localFileInfo.getFileType();
        Long fileSize = localFileInfo.getFileSize();
        LocalDateTime dateModified = localFileInfo.getLastModified();

        return new FileInfoTo(fileName, filePath, fileType, fileSize, dateModified);
    }

    public void saveFileFromServer(ResponseMessage responseMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(fileInfoTo);
        messageHandler.createFileHandler(filePath, fileInfoTo);
    }

    public void copyFileToUserFolder(Path from, Path to) {
        try {
            Files.copy(from, to.resolve(from.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("file copy ex {}");
        }
    }

    public void createNewFolder(String folderName) {
        String relative = operationService.getRelativePath();
        if (operationService.isLocalFolder()) {
            if (operationService.createNewFolder(folderName)) {
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage(relative)));
            }
        } else {
            FileInfoTo dir = new FileInfoTo();
            dir.setFileName(folderName);
            dir.setFileType("folder");
            dir.setFilePath(operationService.getRelativePath());
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), dir));
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage(relative)));
        }
    }

    public void deleteLocalFile(FileInfo fileInfo) {
        Path root = Paths.get(dataModel.getRootPath());
        Path currentPath = root.resolve(fileInfo.getRelativePath());
        try {
            Files.deleteIfExists(currentPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateFileList(FileInfo fileInfo) {
        if (fileInfo.getFileType().equals("folder")) {
            if (operationService.getRelativePath().equals("/")) {
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage("")));
            } else {
                if (fileInfo.getUploadedStatus() == UploadedStatus.NOT_UPLOADED) {
                    operationService.getLocalFileList(fileInfo);
                } else {
                    String relative = Paths.get(dataModel.getRootPath()).relativize(fileInfo.getPath()).toString();
                    messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage(relative)));
                }
            }
        }
    }

    public void sendFolderUpRequest(String relative) {
        operationService.getLocalPath()
        messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FOLDER_UP), new FilesListMessage(relative)));
    }

    public void saveDirectory(ResponseMessage responseMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(fileInfoTo);
        try {
            if (Files.notExists(filePath)) {
                Files.createDirectory(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLocalList(ResponseMessage responseMessage) {
        FilesListMessage listMessage = (FilesListMessage) responseMessage.getAbstractMessageObject();
        operationService.updateLocalList(listMessage.getRelativePath());
    }
}


