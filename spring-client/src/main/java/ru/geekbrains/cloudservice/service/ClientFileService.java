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
import java.util.stream.Stream;

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
            if (responseBody.getFileType().equals("folder")) {
                Set<Path> set;

                try (Stream<Path> pathStream = Files.walk(filePath, Integer.MAX_VALUE)) {
                    set = pathStream.collect(Collectors.toSet());
                    set.forEach(p -> {
                        FileInfo fileInfo = new FileInfo(p);
                        fileInfo.setRelativePath(clientAuthService.getUserFolderPath().relativize(p));
                        FileInfoTo file = getFileInfoTo(fileInfo);
                        if (file.getFileType().equals("folder")) {
                            context.write(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), file), context.newProgressivePromise());
                            log.debug("sent request folder saving");

                        } else {
                            context.writeAndFlush(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), file), context.newProgressivePromise());
                            sendFileToServer(file, p, context);
                        }
                    });
                }
            } else {
                context.writeAndFlush(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE), responseBody), context.newProgressivePromise());
                sendFileToServer(responseBody, filePath, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFileToServer(FileInfoTo responseBody, Path filePath, ChannelHandlerContext context) {
        ChannelFuture sendFileFuture;
        try {
            sendFileFuture = context.writeAndFlush(
                    new DefaultFileRegion(FileChannel.open(filePath, StandardOpenOption.READ), 0L, responseBody.getSize()),
                    context.newProgressivePromise()
            );

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
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //запрос списка файла с сервера
    public void receiveFilesInfoList(Path relativizedPath) {
        if (relativizedPath.equals(Paths.get(""))) {
            clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList("root")));
            return;
        }
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList(relativizedPath.getParent().getFileName().toString())));
    }

    public void addFileListToView(FilesList infoList) {
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
                    if (fileInfoSet.contains(local)) {
                        local.setUploadedStatus("yes");

                    } else if (!fileInfoSet.contains(local)) {
                        local.setUploadedStatus("air");
                    }
                    fileInfoSet.add(local);
                }
            }

        } catch (IOException e) {
            log.warn("FileList updating exception {}", e.getMessage());
        }
    }

    //для рутового каталога
    public void addLocalFilesToView() {
        try {
            addFileList(clientAuthService.getUserFolderPath());
        } catch (IOException e) {
            log.debug("folder receiving data empty");
        }
    }

    public void addLocalFilesToView(Path path) {
        try {
            addFileList(path);
        } catch (IOException e) {
            log.debug("folder receiving data empty");
        }
    }

    private void addFileList(Path path) throws IOException {
        fileInfoSet.clear();
        fileInfoSet.addAll(Files.list(path)
                .map(FileInfo::new)
                .collect(Collectors.toList()));
    }

    public Set<FileInfo> getFileInfoSet() {
        return this.fileInfoSet;
    }
}
