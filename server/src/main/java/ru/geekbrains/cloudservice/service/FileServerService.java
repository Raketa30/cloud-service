package ru.geekbrains.cloudservice.service;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.commands.transmitter.FileReceiver;
import ru.geekbrains.cloudservice.commands.transmitter.FileWriter;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public void saveFile(FileInfo requestBody, ChannelHandlerContext ctx) {
        Path fullPath = Paths.get(activeUser.getServerRootPath()).resolve(requestBody.getRelativePath());
        try {
            SocketChannel socketChannel = (SocketChannel) ctx;
            FileWriter fileWriter = new FileWriter(fullPath);
            FileReceiver fileReceiver = new FileReceiver(socketChannel, fileWriter, requestBody.getFileSize());
            fileReceiver.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public FileOperationResponse checkReceivedFileInfo(FileInfo fileInfo) {
        Path fullPath = Paths.get(activeUser.getServerRootPath()).resolve(fileInfo.getRelativePath());

        if (Files.exists(fullPath)) {
            FileInfo existingFileInfo = new FileInfo(fullPath);
            if (existingFileInfo.equals(fileInfo)) {
                //такой файл существует
                return new FileOperationResponse(FilesOperationResponseType.FILE_ALREADY_EXIST, fileInfo);
            }

        }
        return new FileOperationResponse(FilesOperationResponseType.FILE_READY_TO_SAVE, fileInfo);

    }
}
