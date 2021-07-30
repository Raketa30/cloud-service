package ru.geekbrains.cloudservice.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.api.ServerFileHandler;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.FilesList;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

import java.nio.file.Path;
import java.nio.file.Paths;
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
        FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
        fileInfoTo.setUserId(activeUser.getId());
        Path path = Paths.get(fileInfoTo.getFilePath()).getParent();

        Path fullPath = serverRoot
                .resolve(activeUser.getServerRootPath())
                .resolve(fileInfoTo.getFilePath());

        ServerFileHandler serverFileHandler =  new ServerFileHandler(fullPath, fileInfoTo, userOperationalPathsRepo);
        ChannelPipeline pipeline = ctx.pipeline()
                .addBefore("od", "fh", serverFileHandler);
        log.info(pipeline.toString());
        try {
            serverFileHandler.channelRegistered(ctx);
            serverFileHandler.channelActive(ctx);
        } catch (Exception e) {
            log.debug("serverFileHandler register error: {}", e.getMessage());
        }
        log.debug(pipeline.toString());
    }

    public ResponseMessage checkReceivedFileInfo(FileInfoTo fileInfoTo) {
        Path fullPath = Paths.get(activeUser.getServerRootPath()).resolve(fileInfoTo.getFilePath());
        Optional<FileInfoTo> fileInfoFromDataBase = userOperationalPathsRepo.findFileInfoByRelativePath(fileInfoTo.getFilePath());
        if (fileInfoFromDataBase.isPresent()) {
            //такой файл существует
            return new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_ALREADY_EXIST), fileInfoTo);
        }
        return new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_READY_TO_SAVE), fileInfoTo);
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }


    //метод возвращающий список файлов по указанной ссылке
    public void getFileInfoListForView(RequestMessage requestMessage, ChannelHandlerContext ctx) {
        //подразумевается что сюда прилетает папка родитель
        FilesList filesList = (FilesList) requestMessage.getAbstractMessageObject();
        String parentPath = filesList.getParentPath();

        Optional<List<FileInfoTo>> optionalFileInfos = userOperationalPathsRepo.findFilesByParentPath(parentPath);

        if(optionalFileInfos.isPresent()) {
            filesList.setFileInfoTos(optionalFileInfos.get());
            ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_LIST_SENT), filesList));
        } else {
            ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.EMPTY_LIST)));
        }
    }
}
