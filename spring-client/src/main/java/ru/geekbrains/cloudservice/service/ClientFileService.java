package ru.geekbrains.cloudservice.service;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientHandler;
import ru.geekbrains.cloudservice.commands.RequestMessage;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientFileService {
    private final ClientHandler clientHandler;
    private final ClientAuthService clientAuthService;
    private final Set<FileInfo> fileInfoSet;

    @Autowired
    public ClientFileService(ClientHandler clientHandler, ClientAuthService clientAuthService) {
        this.clientHandler = clientHandler;
        this.clientAuthService = clientAuthService;
        this.fileInfoSet = new HashSet<>();
    }

    public void sendRequestForFileSaving(FileInfo localFileInfo) {
        FileInfoTo fileInfo = getFileInfoTo(localFileInfo);
        clientHandler.sendRequestToServer(new RequestMessage(
                new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), fileInfo));

        log.info("sendRequestForFileSaving {}", localFileInfo);
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

    public void sendFileToServer(FileInfoTo responseBody) {
        Path filePath = clientAuthService.getUserFolderPath().resolve(responseBody.getFilePath());
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

    //запрос списка файла с сервера
    public void receiveFilesInfoList(Path relativizedPath) {
        if (relativizedPath == null || relativizedPath.toString().equals("")) {
            clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList("root")));
        }
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList(relativizedPath.toString())));
    }

    public void addFileListToView(FilesList infoList) {
        try {
            fileInfoSet.clear();

            Path root = clientAuthService.getUserFolderPath();
            if (!infoList.getParentPath().equals("root")) {
                root = root.resolve(infoList.getParentPath());
            }

            fileInfoSet.addAll(Files.list(root)
                    .map(FileInfo::new)
                    .collect(Collectors.toList()));
            List<FileInfoTo> list = infoList.getFileInfoTos();

            if (!list.isEmpty()) {
                for (FileInfoTo f : list) {
                    FileInfo local = new FileInfo(Paths.get(f.getFilePath()), f.getFileName(), f.getFileType(), f.getSize(), f.getLocalDateTime());
                    //если файла нет в локальном хранилище, сохраняем муляж с сервера с пометкой
                    if (fileInfoSet.contains(local)) {
                        local.setUploadedStatus("yes");
                    } else if (!fileInfoSet.contains(local)) {
                        local.setUploadedStatus("air");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLocalFilesToView(Path path) {
        try {
            fileInfoSet.clear();
            fileInfoSet.addAll(Files.list(clientAuthService.getUserFolderPath().resolve(path))
                    .map(FileInfo::new)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.debug("folder receiving data empty");
        }
    }

    //для рутового каталога
    public void addLocalFilesToView() {
        try {
            fileInfoSet.clear();
            fileInfoSet.addAll(Files.list(clientAuthService.getUserFolderPath())
                    .map(FileInfo::new)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.debug("folder receiving data empty");
        }
    }

    public Set<FileInfo> getFileInfoSet() {
        return this.fileInfoSet;
    }
}
