package ru.geekbrains.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.api.ServerClientHandler;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponseType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.FilesList;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FileServerService {
    private final ServerClientHandler clientHandler;
    private final UserOperationalPathsRepo userOperationalPathsRepo;
    private final Path serverRoot;
    private User activeUser;

    public FileServerService(ServerClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userOperationalPathsRepo = new UserOperationalPathsRepo();
        serverRoot = Paths.get("/Users/duckpool/dev/courses/Geekbrains/cloud-service/server/main_root_folder/");
    }

    public void saveFile(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        fileInfoTo.setUserId(activeUser.getId());
        Path fullPath = getFullPath(fileInfoTo);

        clientHandler.createFileHandler(fullPath, fileInfoTo, userOperationalPathsRepo);
    }

    public void saveDirectory(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        fileInfoTo.setUserId(activeUser.getId());
        Path fullPath = getFullPath(fileInfoTo);
        try {
            Optional<FileInfoTo> fileInfoFromDataBase = userOperationalPathsRepo.findFileInfoByRelativePath(fileInfoTo.getFilePath());
            if (fileInfoFromDataBase.isPresent()) {
                clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_ALREADY_EXIST), fileInfoTo));
                if (!Files.exists(fullPath)) {
                    Files.createDirectory(fullPath);
                }
            } else {
                if (!Files.exists(fullPath)) {
                    Files.createDirectory(fullPath);
                }
                userOperationalPathsRepo.saveFileInfo(fileInfoTo);
            }
        } catch (IOException e) {
            log.warn("Saving file throw exception {}", e.getMessage());
        }
    }

    public void checkReceivedFileInfo(RequestMessage requestMessage) {
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        Optional<FileInfoTo> fileInfoFromDataBase = userOperationalPathsRepo.findFileInfoByRelativePath(fileInfoTo.getFilePath());
        if (fileInfoFromDataBase.isPresent()) {
            //такой файл существует
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_ALREADY_EXIST), fileInfoTo));
        } else {
            if (fileInfoTo.getFileType().equals("folder")) {
                clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.DIRECTORY_READY_TO_SAVE), fileInfoTo));
            } else {
                clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_READY_TO_SAVE), fileInfoTo));
            }
        }
    }

    //метод возвращающий список файлов по указанной ссылке
    public void getFileInfoListForView(RequestMessage requestMessage) {
        //подразумевается что сюда прилетает папка родитель
        FilesList filesList = (FilesList) requestMessage.getAbstractMessageObject();
        String parentPath = filesList.getParentPath();
        Optional<List<FileInfoTo>> optionalFileInfos = userOperationalPathsRepo.findFilesByParentPath(parentPath);

        if (optionalFileInfos.isPresent()) {
            filesList.setFileInfoTos(optionalFileInfos.get());
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.FILE_LIST_SENT), filesList));
        } else {
            clientHandler.sendResponse(new ResponseMessage(new FileOperationResponse(FileOperationResponseType.EMPTY_LIST)));
        }
    }

    private Path getResolvedPath(FileInfoTo fileInfoTo) {
        return Paths.get(activeUser.getServerRootPath()).resolve(fileInfoTo.getFilePath());
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    private Path getFullPath(FileInfoTo fileInfoTo) {
        return serverRoot
                .resolve(activeUser.getServerRootPath())
                .resolve(fileInfoTo.getFilePath());
    }
}
