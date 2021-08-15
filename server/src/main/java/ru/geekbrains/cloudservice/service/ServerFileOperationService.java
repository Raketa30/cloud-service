package ru.geekbrains.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.dto.FileTO;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.util.Factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServerFileOperationService {
    private final Path userRootFolder;

    public ServerFileOperationService(User activeUser) {
        this.userRootFolder = Factory.getServerFolderPath().resolve(activeUser.getServerRootPath());
    }

    public Path getFullPath(String relative) {
        return userRootFolder.resolve(relative);
    }

    public Path getFullPath(Path relative) {
        return userRootFolder.resolve(relative);
    }

    public Path getRelativePath(String fullPath) {
        return userRootFolder.relativize(Paths.get(fullPath));
    }

    public Path getRelativePath(Path fullPath) {
        return userRootFolder.relativize(fullPath);
    }

    public Path getParentPath(Path path) {
        if (userRootFolder.equals(path)) {
            return userRootFolder;
        }
        return path.getParent();
    }

    public String getRelativeParentPath(String relative) {
        Path fullPath = getFullPath(relative);
        if(userRootFolder.equals(fullPath) || userRootFolder.equals(fullPath.getParent())) {
            return "";
        }
        return userRootFolder.relativize(fullPath.getParent()).toString();
    }

    public FileInfo getFileInfoFromTO(FileTO fileTO) {
        return new FileInfo(
                Paths.get(fileTO.getRelativePath()),
                fileTO.getFilename(),
                fileTO.getType(),
                fileTO.getSize(),
                fileTO.getLastMod()
        );
    }

    public FileTO getFileTOFromFileInfo(FileInfo info) {
        return new FileTO(
                info.getFilename(),
                getRelativePath(info.getPath()).toString(),
                info.getType(),
                info.getSize(),
                info.getLastModified()
        );
    }

    public List<FileInfo> getFileInfoList(Path relative) {
        Path fullPath = userRootFolder.resolve(relative);
        List<FileInfo> fileInfoList = new ArrayList<>();
        try {
            fileInfoList.addAll(Files.list(fullPath)
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
        return fileInfoList;
    }

    public List<FileTO> getFileInfoList(String relative) {
        return getFileInfoTOList(Paths.get(relative));
    }

    public List<FileTO> getFileInfoTOList(Path relative) {
        List<FileInfo> infos = getFileInfoList(relative);
        List<FileTO> tos = new ArrayList<>();
        for (FileInfo info : infos) {
            tos.add(getFileTOFromFileInfo(info));
        }
        return tos;
    }

}
