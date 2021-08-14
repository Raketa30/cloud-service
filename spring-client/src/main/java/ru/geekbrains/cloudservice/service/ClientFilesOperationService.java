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
import ru.geekbrains.cloudservice.model.UploadedStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientFilesOperationService {
    private final DataModel dataModel;

    @Autowired
    public ClientFilesOperationService(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    //получаем список локальных файлов
    public void getLocalFileList(FileInfo fileInfo) {
        Path path = fileInfo.getPath();
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
            setLocalRelativePath(path);
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
                    local.setUploadedStatus(UploadedStatus.AIR);
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
                fileInfos.add(i);
            } else {
                for (FileInfo j : fileInfos) {
                    if (j.equals(i)) {
                        j.setUploadedStatus(UploadedStatus.UPLOADED);
                        break;
                    }
                }
            }
        }
    }

    public void addFileListFromServer(ResponseMessage responseMessage) {
        FilesListMessage listInfo = (FilesListMessage) responseMessage.getAbstractMessageObject();
        dataModel.setRelativePath(listInfo.getRelativePath());
        dataModel.getFileInfos().clear();
        Path local = Paths.get(dataModel.getRootPath()).resolve(listInfo.getRelativePath());
        if (Files.exists(local)) {
            getLocalFileList(new FileInfo(local));
        }
        updateFileListInfo(listInfo);
    }

    public void updateLocalList(String relativePath) {
        dataModel.setRelativePath(relativePath);
        Path local = Paths.get(dataModel.getRootPath()).resolve(relativePath);
        if (Files.exists(local)) {
            getLocalFileList(new FileInfo(local));
        }
    }

    public boolean createNewFolder(String folderName) {
        try {
            Path path = getLocalPath(folderName);
            if (!checkPath(path)) {
                Files.createDirectory(path);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkPath(Path path) {
        return Files.exists(path);
    }

    public Path getLocalPath(String fileName) {
        return Paths.get(dataModel.getRootPath()).resolve(dataModel.getRelativePath()).resolve(fileName);
    }

    public boolean isLocalFolder() {
        return Files.exists(Paths.get(dataModel.getRootPath()).resolve(dataModel.getRelativePath()));
    }

    public String getRelativePath() {
        return dataModel.getRelativePath();
    }

    public String getRelativePath(String fileName) {
        return Paths.get(dataModel.getRelativePath()).resolve(fileName).toString();
    }

    private void setLocalRelativePath(Path path) {
        if (dataModel.getRootPath().equals(path.toString())) {
            return;
        }
        Path relative = Paths.get(dataModel.getRootPath()).relativize(path).getParent();
        dataModel.setRelativePath(relative.toString());
    }
}
