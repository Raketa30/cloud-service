package ru.geekbrains.cloudservice.service;

import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.FilesListMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileListViewService {
    private final DataModel dataModel;

    @Autowired
    public FileListViewService(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    //получаем список локальных файлов
    public void getLocalFileList(Path path) {
        try {
            List<FileInfo> localList = new ArrayList<>();
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
            dataModel.setFileInfos(localList);
        } catch (IOException e) {
            log.warn("get local file list exc");
        }
    }

    //получаем список файлов с сервера и объеденяем с локальным
    public void updateFileListInfo(FilesListMessage listFromServer) {
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
        } catch (Exception e) {
            log.warn("FileList updating exception {}", e.getMessage());
        }
    }

    private void merge(List<FileInfo> serverList) {
        ObservableList<FileInfo> fileInfos = dataModel.getFileInfos();
        for (FileInfo i : serverList) {
            if (!fileInfos.contains(i)) {
                i.setUploadedStatus("air");
                fileInfos.add(i);
            } else {
                for (FileInfo j : fileInfos) {
                    if (j.equals(i)) {
                        j.setUploadedStatus("yes");
                        break;
                    }
                }
            }
        }
    }

    public void addFileListFromServer(ResponseMessage responseMessage) {
        FilesListMessage listInfo = (FilesListMessage) responseMessage.getAbstractMessageObject();
        if(listInfo.getParentPath().equals("root")) {
            listInfo.setParentPath("");
        }
        if (listInfo.getParentPath().equals(dataModel.getRelativePath())) {
            updateFileListInfo(listInfo);
        }
    }

    public void updateListView(Path path) {
        dataModel.setRelativePath(path);
        getLocalFileList(path);
    }
}
