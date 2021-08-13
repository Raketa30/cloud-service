package ru.geekbrains.cloudservice.service;

import io.netty.channel.DefaultFileRegion;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.api.ServerMessageHandler;
import ru.geekbrains.cloudservice.commands.FilesListMessage;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponseType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.util.Factory;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FileServerService {
    private final ServerMessageHandler clientHandler;
    private Path userFolderPath;

    public FileServerService(ServerMessageHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void saveFile(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        Path fullPath = getFullPath(fileInfoTo);
        clientHandler.createFileHandler(fullPath, fileInfoTo);
    }

    private void saveDirectory(FileInfoTo fileInfoTo) {
        Path fullPath = getFullPath(fileInfoTo);
        try {
            if (Files.exists(fullPath)) {
                clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_ALREADY_EXIST), fileInfoTo));
            } else {
                if (!Files.exists(fullPath)) {
                    Files.createDirectory(fullPath);
                }
            }
        } catch (IOException e) {
            log.warn("Saving file throw exception {}", e.getMessage());
        }
    }

    public void checkReceivedFileInfo(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        Path fullPath = getFullPath(fileInfoTo);
        if (Files.exists(fullPath)) {
            //такой файл существует
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_ALREADY_EXIST), fileInfoTo));
        } else {
            if (fileInfoTo.getFileType().equals("folder")) {
                saveDirectory(fileInfoTo);
            } else {
                clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_READY_TO_SAVE), fileInfoTo));
            }
        }
    }

    //метод возвращающий список файлов по указанной ссылке
    public void getFileInfoListForView(RequestMessage requestMessage) {
        FilesListMessage filesListMessage = (FilesListMessage) requestMessage.getAbstractMessageObject();
        String relative = filesListMessage.getParentPath();
        Path parent = getServerPath(relative);
        List<FileInfo> fileInfo = getFileInfoList(parent);
        List<FileInfoTo> fileInfoTos = getFileInfoToList(fileInfo);
        FilesListMessage message = new FilesListMessage(relative);
        message.setFileInfoTos(fileInfoTos);
        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_LIST_SENT), message));
    }

    private List<FileInfoTo> getFileInfoToList(List<FileInfo> fileInfo) {
        List<FileInfoTo> list = new ArrayList<>();
        for (FileInfo info : fileInfo) {
            list.add(getFileInfoTo(info));
        }
        return list;
    }

    private FileInfoTo getFileInfoTo(FileInfo localFileInfo) {
        String fileName = localFileInfo.getFilename();
        String filePath = userFolderPath.relativize(localFileInfo.getPath()).toString();
        String fileType = localFileInfo.getFileType();
        Long fileSize = localFileInfo.getFileSize();
        LocalDateTime dateModified = localFileInfo.getLastModified();

        return new FileInfoTo(fileName, filePath, fileType, fileSize, dateModified);
    }

    private List<FileInfo> getFileInfoList(Path parent) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        try {

            fileInfoList.addAll(Files.list(parent)
                    .filter(p -> {
                        try {
                            return !Files.isHidden(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .map(FileInfo::new)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.warn("get server file list exc");
        }
        System.out.println(fileInfoList);
        return fileInfoList;
    }

    public void setUserFolderPath(User activeUser) {
        Path serverRoot = Factory.getServerFolderPath();
        this.userFolderPath = serverRoot.resolve(activeUser.getServerRootPath());
    }

    private Path getFullPath(FileInfoTo fileInfoTo) {
        return userFolderPath
                .resolve(fileInfoTo.getFilePath());
    }

    private Path getServerPath(String parent) {
        return userFolderPath
                .resolve(parent);
    }

    public void downloadFile(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_SENT), fileInfoTo));
        sendFileToClient(fileInfoTo, getFullPath(fileInfoTo));
    }

    private void sendFileToClient(FileInfoTo responseBody, Path filePath) {
        try {
            clientHandler.sendFileToClient(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }
}
