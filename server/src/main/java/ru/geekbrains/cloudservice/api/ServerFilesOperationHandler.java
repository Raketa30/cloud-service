package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.FileServerService;

@Slf4j
public class ServerFilesOperationHandler {
    private final FileServerService fileServerService;

    public ServerFilesOperationHandler() {
        fileServerService = new FileServerService();
    }

    public void processRequest(RequestMessage requestMessage, ChannelHandlerContext ctx, User activeUser) {
        log.info("received file request {}", requestMessage.getRequest());
        if(activeUser == null) {
            ctx.channel().writeAndFlush(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_WRONG)));
        } else {
            fileServerService.setActiveUser(activeUser);
            FileOperationRequestType fileOperationRequestType = (FileOperationRequestType) requestMessage.getRequest().getRequestCommandType();

            switch (fileOperationRequestType) {
                case FILES_LIST:
                    fileServerService.getFileInfoListForView(requestMessage, ctx);
                    break;

                case SAVE_FILE_REQUEST:
                    FileInfoTo fileInfoTo = (FileInfoTo) requestMessage.getAbstractMessageObject();
                    ctx.channel().writeAndFlush(fileServerService.checkReceivedFileInfo(fileInfoTo));
                    break;

                case SAVE_FILE:
                    fileServerService.saveFile(requestMessage, ctx);
                    break;

                case DOWNLOAD_FILE:
                    break;

                case DOWNLOAD_FILE_LIST:
                    break;
            }
        }
    }
}
