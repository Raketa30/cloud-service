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

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.time.LocalDateTime;

@Slf4j
@Service
public class ClientFileService {
    private final ClientMessageHandler clientMessageHandler;
    private final DataModel dataModel;

    @Autowired
    public ClientFileService(ClientMessageHandler clientMessageHandler, DataModel dataModel) {
        this.clientMessageHandler = clientMessageHandler;
        this.dataModel = dataModel;
    }

    public void sendRequestForFileSaving(FileInfo localFileInfo) {
        FileInfoTo fileInfo = getFileInfoTo(localFileInfo);
        clientMessageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), fileInfo));
    }

    public void sendFileToServer(ResponseMessage responseMessage) {
        FileInfoTo responseBody = (FileInfoTo) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(responseBody);
        try {
            if (!Files.isHidden(filePath) && Files.isReadable(filePath)) {
                clientMessageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), responseBody));
                sendFileToServer(responseBody, filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFileToServer(FileInfoTo responseBody, Path filePath) {
        try {
            clientMessageHandler.sendFileToServer(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }

    public void sendRequestForFileDownloading(FileInfo fileInfo) {
        FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
        clientMessageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DOWNLOAD_FILE), fileInfoTo));
    }

    public void sendRequestForDeleting(FileInfo fileInfo) {
        FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
        clientMessageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.DELETE_FILE), fileInfoTo));
    }

    private Path getFilePath(FileInfoTo to) {
        Path root = Paths.get(dataModel.getRootPath());
        return root.resolve(to.getFilePath());
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

    public void saveFileFromServer(ResponseMessage responseMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(fileInfoTo);
        clientMessageHandler.createFileHandler(filePath, fileInfoTo);
    }

    public void copyFileToUserFolder(Path from, Path to) {
        try {
            Files.copy(from, to.resolve(from.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("file copy ex {}");
        }
    }

    public void createNewFolder(String text, Path path) {
        try {
            Files.createDirectory(path.resolve(text));
            updateFileList(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLocalFile(Path currentPath) {
        try {
            Files.deleteIfExists(currentPath);
            updateFileList(currentPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateFileList(Path currentPath) {
        Path root = Paths.get(dataModel.getRootPath());
        if (currentPath.equals(root)) {
            clientMessageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage("root")));
        } else {
            Path message = root.relativize(currentPath);
            clientMessageHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesListMessage(message.toString())));
        }
    }

}


