package ru.geekbrains.cloudservice.service;

import io.netty.channel.DefaultFileRegion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientMessageHandler;
import ru.geekbrains.cloudservice.commands.impl.RequestMessage;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.files.FileOperationRequest;
import ru.geekbrains.cloudservice.commands.impl.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.commands.impl.files.FilesListMessage;
import ru.geekbrains.cloudservice.commands.impl.files.FolderUpMessage;
import ru.geekbrains.cloudservice.dto.FileTO;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.model.FileInfo;

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
        FileTO to = getFileInfoTo(fileInfo);
        if (Files.exists(filePath)) {
            if (Files.isDirectory(filePath)) {
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), to));
                try {
                    Files.walk(filePath).forEach(p -> {
                        FileInfo file = new FileInfo(p);
                        if (Files.isDirectory(p)) {
                            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), to));
                        } else {
                            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), to));
                            sendFileToServer(p, file);
                        }
                    });
                } catch (IOException e) {
                    log.warn("send file to server error {}", e.getMessage());
                }
            } else {
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), to));
                sendFileToServer(filePath, fileInfo);
            }
        }
    }

    private void sendFileToServer(Path filePath, FileInfo fileInfo) {
        try {
            messageHandler.sendFileToServer(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, fileInfo.getSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }

    public void sendRequestForFileDownloading(FileInfo fileInfo) {
        FileTO fileTO = getFileInfoTo(fileInfo);
        if (fileInfo.getType().equals("folder")) {
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DOWNLOAD_DIRECTORY), fileTO));
        } else {
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DOWNLOAD_FILE), fileTO));
        }
    }

    public void sendRequestForDeleting(FileInfo fileInfo) {
        FileTO fileTO = getFileInfoTo(fileInfo);
        messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DELETE_FILE), fileTO));
    }

    private Path getFilePath(FileTO to) {
        Path root = Paths.get(dataModel.getRootPath());
        return root.resolve(to.getRelativePath());
    }

    private FileTO getFileInfoTo(FileInfo localFileInfo) {
        String fileName = localFileInfo.getFilename();
        String filePath = localFileInfo.getRelative() == null ?
                Paths.get(dataModel.getRootPath()).relativize(localFileInfo.getPath()).toString() :
                localFileInfo.getRelative().toString();
        String fileType = localFileInfo.getType();
        Long fileSize = localFileInfo.getSize();
        LocalDateTime dateModified = localFileInfo.getLastModified();

        return new FileTO(fileName, filePath, fileType, fileSize, dateModified);
    }

    public void saveFileFromServer(ResponseMessage responseMessage) {
        FileTO fileTO = (FileTO) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(fileTO);
        messageHandler.createFileHandler(filePath, fileTO);
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
            FileTO dir = new FileTO();
            dir.setFilename(folderName);
            dir.setType("folder");
            dir.setRelativePath(operationService.getRelativePath());
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), dir));
            messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage(relative)));
        }
    }

    public void deleteFile(FileInfo fileInfo, String relative) {
        switch (fileInfo.getUploadedStatus()) {
            case UPLOADED:
            case AIR:
                FileTO file = new FileTO();
                file.setRelativePath(fileInfo.getRelative().toString());
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DELETE_FILE), file));
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage(relative)));
                break;
            case NOT_UPLOADED:
                break;
        }
    }

    public void updateFileList(FileInfo fileInfo) {
        if (fileInfo.getType().equals("folder")) {
            if (operationService.getRelativePath().equals("/")) {
                messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage("")));
            } else {
                switch (fileInfo.getUploadedStatus()) {
                    case AIR:
                        String relative = fileInfo.getRelative().toString();
                        messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage(relative)));
                        break;
                    case UPLOADED:
                    case NOT_UPLOADED:
                        operationService.getLocalFileList(fileInfo);
                        break;
                }
            }
        }
    }

    public void sendFolderUpRequest(String relative) {
        messageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FOLDER_UP), new FolderUpMessage(relative)));
    }

    public void saveDirectory(ResponseMessage responseMessage) {
        FileTO fileTO = (FileTO) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(fileTO);
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
        operationService.updateLocalList(listMessage.getParent());
    }

    public void addNewFile(String text) {
        if (operationService.addNewFile(text)) {
            operationService.updateLocalList(dataModel.getRelativePath());
        }
    }
}


