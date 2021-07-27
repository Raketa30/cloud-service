package ru.geekbrains.cloudservice.service;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileServerService {
    private User activeUser;
    private UserOperationalPathsRepo userOperationalPathsRepo;

    public FileServerService() {

        this.userOperationalPathsRepo = new UserOperationalPathsRepo();
    }

    //создаем папку для пользователя
    public void createUserRootDirectory(String username) {

    }

    public void saveFile(RequestMessage requestMessage, ChannelHandlerContext ctx) {
        FileInfo fileInfo = (FileInfo) requestMessage.getAbstractMessageObject();
        Path fullPath = Paths.get(activeUser.getServerRootPath()).resolve(fileInfo.getRelativePath());

    }

    public void saveFileList(RequestMessage requestMessage) {
    }

    public FileInfo getFile(RequestMessage requestMessage) {
        return null;
    }

    public List<FileInfo> getFileList(RequestMessage requestMessage) {
        List<FileInfo> fileInfoList = new ArrayList<>();

        return fileInfoList;
    }

    public ResponseMessage checkReceivedFileInfo(FileInfo fileInfo) {
        Path fullPath = Paths.get(activeUser.getServerRootPath()).resolve(fileInfo.getRelativePath());

        if (Files.exists(fullPath)) {
            FileInfo existingFileInfo = new FileInfo(fullPath);
            if (existingFileInfo.equals(fileInfo)) {
                //такой файл существует
                return new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_ALREADY_EXIST), existingFileInfo) ;
            }

        }
        return new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_READY_TO_SAVE));

    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }
}
