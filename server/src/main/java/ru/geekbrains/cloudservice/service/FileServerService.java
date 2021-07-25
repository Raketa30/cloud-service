package ru.geekbrains.cloudservice.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class FileServerService {
    private User activeUser;
    private UserOperationalPathsRepo userOperationalPathsRepo;

    public FileServerService(UserOperationalPathsRepo userOperationalPathsRepo) {
        this.userOperationalPathsRepo = userOperationalPathsRepo;
    }

    //создаем папку для пользователя
    public void createUserRootDirectory(String username) {

    }

    public void saveFile(FileInfo requestBody) {

    }

    public void saveFileList(FileInfo requestBody) {
    }

    public FileInfo getFile(FileInfo requestBody) {
        return null;
    }

    public List<FileInfo> getFileList(FileInfo requestBody) {
        List<FileInfo> fileInfoList = new ArrayList<>();

        return fileInfoList;
    }
}
