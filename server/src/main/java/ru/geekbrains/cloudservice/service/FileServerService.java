package ru.geekbrains.cloudservice.service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponse;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserOperationalPathsRepo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Path fullPath = Paths.get("/Users/duckpool/dev/courses/Geekbrains/cloud-service/server/main_root_folder" +
                activeUser.getServerRootPath()).resolve(fileInfo.getFilePath());
        try {
            File file = fullPath.toFile();
            if (!Files.exists(fullPath)) {
                file.createNewFile();
            }
            ByteBuf in = ctx.alloc().buffer();
            ByteBuffer nioBuffer = in.nioBuffer();
            FileOutputStream fos = new FileOutputStream(file);
            FileChannel channel = fos.getChannel();
            while (nioBuffer.hasRemaining()) {
                channel.write(nioBuffer);
            }
            channel.close();
            fos.close();
            ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_SAVED), fileInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ctx.writeAndFlush(new ResponseMessage(new FileOperationResponse(FilesOperationResponseType.FILE_SAVING_PROBLEM), fileInfo));

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
}
