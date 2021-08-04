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
import ru.geekbrains.cloudservice.model.FilesList;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientFileService {
    private final ClientHandler clientHandler;
    private final ClientAuthService clientAuthService;
    private final List<FileInfo> fileListForView;


    @Autowired
    public ClientFileService(ClientHandler clientHandler, ClientAuthService clientAuthService) {
        this.clientHandler = clientHandler;
        this.clientAuthService = clientAuthService;
        this.fileListForView = new CopyOnWriteArrayList<>();
    }

    public void sendRequestForFileSaving(FileInfo localFileInfo) {
        FileInfoTo fileInfo = getFileInfoTo(localFileInfo);
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), fileInfo));
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

    public void sendDirectoryToServer(ResponseMessage responseMessage) {
        FileInfoTo responseBody = (FileInfoTo) responseMessage.getAbstractMessageObject();
        Path filePath = getFilePath(responseBody);
        FileInfo fileInfo = new FileInfo(filePath);
        fileInfo.setRelativePath(clientAuthService.getUserFolderPath().relativize(filePath));
        FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), fileInfoTo));
        //пытаюсь передать все папки

//        Set<Path> set;
//
//        try (Stream<Path> pathStream = Files.walk(filePath, 1)) {
//            set = pathStream.collect(Collectors.toSet());
//            if(set.size() == 1) {
//                FileInfo fileInfo = new FileInfo(filePath);
//                fileInfo.setRelativePath(clientAuthService.getUserFolderPath().relativize(filePath));
//                FileInfoTo fileInfoTo = getFileInfoTo(fileInfo);
//
//                clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_DIRECTORY), fileInfoTo));
//            } else {
//                set.forEach(p -> {
//                    FileInfo fileInfo = new FileInfo(p);
//                    fileInfo.setRelativePath(clientAuthService.getUserFolderPath().relativize(p));
//                    sendRequestForFileSaving(fileInfo);
//                });
//            }
//        } catch (IOException e) {
//            log.warn("Folder sending problem");
//        }
    }

    private Path getFilePath(FileInfoTo responseBody) {
        return clientAuthService.getUserFolderPath().resolve(responseBody.getFilePath());
    }

    private void sendFileToServer(FileInfoTo responseBody, Path filePath) {
        try {
            clientHandler.sendFileToServer(new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()));
        } catch (IOException e) {
            log.warn("file sending ex {}", e.getMessage());
        }
    }

    //запрос списка файла с сервера
    private void sentServerFileListRequest(Path relativizedPath) {
        if (relativizedPath == null) {
            clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList("root")));
            return;
        }
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList(relativizedPath.toString())));
    }

    public void addFileListFromServer(FilesList infoList) {
        try {
            Path root = clientAuthService.getUserFolderPath();
            if (!infoList.getParentPath().equals("root")) {
                root = root.resolve(infoList.getParentPath());
            }

            addFileList(root);

            List<FileInfoTo> list = infoList.getFileInfoTos();
            if (!list.isEmpty()) {
                for (FileInfoTo f : list) {
                    FileInfo local = new FileInfo(Paths.get(f.getFilePath()), f.getFileName(), f.getFileType(), f.getSize(), f.getLocalDateTime());
                    //если файла нет в локальном хранилище, сохраняем муляж с сервера с пометкой
                    if (this.fileListForView.contains(local)) {
                        local.setUploadedStatus("yes");

                    } else if (!this.fileListForView.contains(local)) {
                        local.setUploadedStatus("air");
                    }
                    this.fileListForView.add(local);
                }
            }

        } catch (Exception e) {
            log.warn("FileList updating exception {}", e.getMessage());
        }
    }

    //для рутового каталога

    public void getFilesList(Path relative) {
        try {
            addFileList(relative);
            sentServerFileListRequest(relative);
        } catch (Exception e) {
            log.debug("folder receiving data empty");
        }
    }

    private void addFileList(Path path) throws IOException {
        fileListForView.clear();
        fileListForView.addAll(Files.list(path)
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

    }




    public List<FileInfo> getFileListForView() {
        return this.fileListForView;
    }
}
