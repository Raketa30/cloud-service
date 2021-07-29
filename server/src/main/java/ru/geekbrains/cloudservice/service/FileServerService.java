package ru.geekbrains.cloudservice.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.api.ServerFileHandler;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FileServerService {
    private User activeUser;
    private final UserOperationalPathsRepo userOperationalPathsRepo;
    private final Path serverRoot;

    public FileServerService() {
        this.userOperationalPathsRepo = new UserOperationalPathsRepo();
        serverRoot = Paths.get("/Users/duckpool/dev/courses/Geekbrains/cloud-service/server/main_root_folder/");
    }

    public void saveFile(RequestMessage requestMessage, ChannelHandlerContext ctx) {
        FileInfo fileInfo = (FileInfo) requestMessage.getAbstractMessageObject();
        Path fullPath = serverRoot
                .resolve(activeUser.getServerRootPath())
                .resolve(fileInfo.getFilePath());


        ServerFileHandler serverFileHandler =  new ServerFileHandler(fullPath, fileInfo);
        ChannelPipeline pipeline = ctx.pipeline()
                .addBefore("od", "fh", serverFileHandler);
        log.info(pipeline.toString());
        try {
            serverFileHandler.channelRegistered(ctx);
            serverFileHandler.channelActive(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(pipeline);
    }

    public void saveFileList(RequestMessage requestMessage) {
    }

    public FileInfo getFile(RequestMessage requestMessage) {
        return null;
    }

    public List<FileInfo> getFileList(RequestMessage requestMessage) {
        List<FileInfo> localFileInfoList = new ArrayList<>();
        return localFileInfoList;
    }

    public ResponseMessage checkReceivedFileInfo(FileInfo fileInfo) {
        Path fullPath = Paths.get(activeUser.getServerRootPath()).resolve(fileInfo.getFilePath());
        Optional<FileInfo> fileInfoFromDataBase = userOperationalPathsRepo.findFileInfoByRelativePath(fileInfo.getFilePath());
        if (fileInfoFromDataBase.isPresent()) {
            //такой файл существует
            return new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_ALREADY_EXIST), fileInfo);
        }
        return new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_READY_TO_SAVE), fileInfo);
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }


    //метод возвращающий список файлов по указанной ссылке
    public void getFileInfoListForView(RequestMessage requestMessage, ChannelHandlerContext ctx) {
        //подразумевается что сюда прилетает папка родитель
        FileInfo fileInfo = (FileInfo) requestMessage.getAbstractMessageObject();
        String filePath = fileInfo.getFilePath();

        Optional<List<FileInfo>> optionalFileInfos = userOperationalPathsRepo.findFilesByParentPath(filePath);
    }
}
