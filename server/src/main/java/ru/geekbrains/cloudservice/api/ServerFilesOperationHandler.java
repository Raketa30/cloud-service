package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.RequestMessage;
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

    public void processRequest(RequestMessage requestMessage, ChannelHandlerContext ctx) {
        log.info("received file request {}", requestMessage.getRequest());
        FileOperationRequestType fileOperationRequestType = (FileOperationRequestType) requestMessage.getRequest().getRequestCommandType();
        switch (fileOperationRequestType) {
            case SAVE_FILE_REQUEST:
                FileInfo fileInfo = (FileInfo) requestMessage.getAbstractMessageObject();
                ctx.channel().writeAndFlush(fileServerService.checkReceivedFileInfo(fileInfo));
                break;

            case SAVE_FILE:
                fileServerService.saveFile(requestMessage, ctx);
                break;

            case SAVE_FILES_LIST_REQUEST:
                fileServerService.saveFileList(requestMessage);
                break;

            case DOWNLOAD_FILE:
                FileInfo file = fileServerService.getFile(requestMessage);
                ctx.channel().writeAndFlush(file);
                break;

            case DOWNLOAD_FILE_LIST:
                List<FileInfo> fileInfoList = fileServerService.getFileList(requestMessage);
                break;
        }
    }
}
