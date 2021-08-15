package ru.geekbrains.cloudservice.util;

import ru.geekbrains.cloudservice.api.ServerMessageHandler;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserRepo;
import ru.geekbrains.cloudservice.service.ServerAuthService;
import ru.geekbrains.cloudservice.service.ServerFileOperationService;
import ru.geekbrains.cloudservice.service.ServerFileService;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Factory {
    private Factory() {

    }

    public static Path getServerFolderPath() {
        return Paths.get("/Users/duckpool/dev/courses/Geekbrains/cloud-service/server/main_root_folder/");
    }

    public static UserRepo getUserRepo() {
        return new UserRepo();
    }

    public static ServerAuthService getAuthService() {
        return new ServerAuthService();
    }

    public static ServerFileService getFileService(ServerMessageHandler serverMessageHandler, User user) {
        return new ServerFileService(serverMessageHandler, new ServerFileOperationService(user));
    }
}
