package ru.geekbrains.cloudservice.service;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileListViewService {
    private final ClientHandler clientHandler;
    private final List<FileInfo> localList;
    private boolean updated;

    @Autowired
    public FileListViewService(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.localList = new CopyOnWriteArrayList<>();
        this.updated = false;
    }

    //получаем список локальных файлов
    public void getLocalFileList(Path path, Path relative) {
        localList.clear();
        sentServerFileListRequest(relative);
        try {
            localList.addAll(Files.list(path)
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
            log.warn("get local file list exc");
        }
    }

    //получаем список файлов с сервера и объеденяем с локальным
    public void updateFileListInfo(FilesList listFromServer) {
        try {
            List<FileInfoTo> list = listFromServer.getFileInfoTos();
            List<FileInfo> serverList = new ArrayList<>();
            if (!list.isEmpty()) {
                for (FileInfoTo f : list) {
                    FileInfo local = new FileInfo(Paths.get(f.getFilePath()), f.getFileName(), f.getFileType(), f.getSize(), f.getLocalDateTime());
                    serverList.add(local);
                }
            }
            merge(serverList);
            this.updated = true;
        } catch (Exception e) {
            log.warn("FileList updating exception {}", e.getMessage());
        }
    }

    private void merge(List<FileInfo> serverList) {
        for (FileInfo i : serverList) {
            if (!localList.contains(i)) {
                i.setUploadedStatus("air");
                localList.add(i);
            } else {
                for (FileInfo j : localList) {
                    if (j.equals(i)) {
                        j.setUploadedStatus("yes");
                        break;
                    }
                }
            }
        }
        System.out.println(localList);
    }

    //запрос списка файла с сервера
    private void sentServerFileListRequest(Path relativizedPath) {
        if (relativizedPath.toString().equals("")) {
            clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList("root")));
            return;
        }
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.FILES_LIST), new FilesList(relativizedPath.toString())));
        this.updated = false;
    }


    public void addFileListFromServer(ResponseMessage responseMessage) {
        FilesList listInfo = (FilesList) responseMessage.getAbstractMessageObject();
        updateFileListInfo(listInfo);
    }

    public List<FileInfo> getFileListForView() {
        return this.localList;
    }

    public boolean isUpdated() {
        return updated;
    }
}
