package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.FileServerService;

import java.util.List;
@Slf4j
public class ServerFilesOperationHandler {
    private final FileServerService fileServerService;

    private User activeUser;

    public ServerFilesOperationHandler() {
        fileServerService = new FileServerService();
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
        fileServerService.setActiveUser(activeUser);
    }

    public void processRequest(Request<FileInfo, FileOperationRequestType> request, ChannelHandlerContext ctx) {
        log.info("received file request {}", request.getType());
        switch (request.getType()) {
            case SAVE_FILE_REQUEST:
                FileInfo fileInfo = request.getRequestBody();
                ctx.writeAndFlush(fileServerService.checkReceivedFileInfo(fileInfo));
                break;

            case SAVE_FILE:
                fileServerService.saveFile(request.getRequestBody(), ctx);
                break;

            case SAVE_FILES_LIST_REQUEST:
                fileServerService.saveFileList(request.getRequestBody());
                break;

            case DOWNLOAD_FILE:
                FileInfo file = fileServerService.getFile(request.getRequestBody());
                ctx.writeAndFlush(file);
                break;

            case DOWNLOAD_FILE_LIST:
                List<FileInfo> fileInfoList = fileServerService.getFileList(request.getRequestBody());
                break;
        }
    }
}
