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
public class ServerFileService {
    private final ServerMessageHandler clientHandler;
    private Path userFolderPath;

    public ServerFileService(ServerMessageHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void saveFile(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        Path fullPath = getFullPath(fileInfoTo);
        clientHandler.createFileHandler(fullPath, fileInfoTo);
    }

    public void saveDirectory(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        Path fullPath = getFullPath(fileInfoTo);
        if (Files.notExists(fullPath)) {
            try {
                Files.createDirectory(fullPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //метод возвращающий список файлов по указанной ссылке
    public void getFileInfoListForView(RequestMessage requestMessage) {
        FilesListMessage filesListMessage = (FilesListMessage) requestMessage.getAbstractMessageObject();
        Path serverPath = getServerPath(filesListMessage.getRelativePath());
        if (Files.notExists(serverPath)) {
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_NOT_EXIST), filesListMessage));
        } else {
            List<FileInfoTo> fileInfoTos = getFileInfoToList(serverPath);
            filesListMessage.setFileInfoTos(fileInfoTos);
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_LIST_SENT), filesListMessage));
        }
    }

    public void folderUp(RequestMessage requestMessage) {
        FilesListMessage filesListMessage = (FilesListMessage) requestMessage.getAbstractMessageObject();
        Path serverPath = getServerPath(filesListMessage.getRelativePath());
        Path parent = serverPath.getParent();
        List<FileInfoTo> fileInfoTos = getFileInfoToList(parent);
        filesListMessage.setFileInfoTos(fileInfoTos);
        String relative = userFolderPath.relativize(parent).toString();
        filesListMessage.setRelativePath(relative);
        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_LIST_SENT), filesListMessage));
    }

    private List<FileInfoTo> getFileInfoToList(Path parent) {
        List<FileInfo> fileInfo = getFileInfoList(parent);
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

    public void downloadDirectory(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        Path fullPath = getFullPath(fileInfoTo);
        if (Files.isDirectory(fullPath)) {
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.DIRECTORY_SENT), fileInfoTo));
            try {
                Files.walk(fullPath).forEach(p -> {
                    FileInfoTo file = getFileInfoTo(new FileInfo(p));
                    if (Files.isDirectory(p)) {
                        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.DIRECTORY_SENT), file));
                    } else {
                        clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_SENT), file));
                        sendFileToClient(file, p);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFileToClient(FileInfoTo responseBody, Path filePath) {
        try {
            clientHandler.sendFileToClient(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }

    public void deleteFile(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        Path fullPath = getFullPath(fileInfoTo);
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
