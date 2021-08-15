package ru.geekbrains.cloudservice.service;

import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.files.FilesListMessage;
import ru.geekbrains.cloudservice.dto.FileTO;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.UploadedStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientFilesOperationService {
    private final DataModel dataModel;

    @Autowired
    public ClientFilesOperationService(DataModel dataModel) {
        this.dataModel = dataModel;
    }

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

    public void updateFileListInfo(FilesListMessage listFromServer) {
        try {
            List<FileTO> list = listFromServer.getFileTOS();
            List<FileInfo> serverList = new ArrayList<>();
            if (!list.isEmpty()) {
                for (FileTO f : list) {
                    FileInfo local = new FileInfo(Paths.get(f.getRelativePath()), f.getFilename(), f.getType(), f.getSize(), f.getLastMod());
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
        dataModel.setRelativePath(listInfo.getParent());
        dataModel.getFileInfos().clear();
        Path local = Paths.get(dataModel.getRootPath()).resolve(listInfo.getParent());
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
        Path relative = Paths.get(dataModel.getRootPath()).relativize(path);
        dataModel.setRelativePath(relative.toString());
    }

    public void createUserFolder(Path path, String username) {
        File file = new File(username.concat(".txt"));
        try(FileWriter fileWriter = new FileWriter(file, false)) {
            Files.createDirectories(path);
            fileWriter.write(path.toString());
        } catch (IOException e) {
            log.warn("Problems with write setting file: {} ", e.getMessage());
        }
    }

    public String getUserFolder(String username) {
        File userSettings = new File(username.concat(".txt"));
        String result = "";
        if(userSettings.exists()) {
            try (Scanner scanner = new Scanner(userSettings)) {
                while (scanner.hasNext()) {
                    result = scanner.nextLine();
                }
            } catch (FileNotFoundException e) {
                log.warn("user folder not found");
            }
        }

        return result;
    }

    public boolean addNewFile(String path) {
        try {
            Path from = Paths.get(path);
            Path to = Paths.get(dataModel.getRootPath()).resolve(dataModel.getRelativePath()).resolve(from.getFileName());
            Files.copy(from, to);
            return true;
        } catch (IOException e) {
            log.warn("add new file exception {}", e.getMessage());
        }
        return false;
    }
}
