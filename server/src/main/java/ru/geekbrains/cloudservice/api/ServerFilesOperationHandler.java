package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.impl.RequestMessage;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthResponseType;
import ru.geekbrains.cloudservice.commands.impl.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.ServerFileService;
import ru.geekbrains.cloudservice.util.Factory;

@Slf4j
public class ServerFilesOperationHandler {
    private ServerFileService serverFileService;
    private final ServerMessageHandler serverMessageHandler;
    private User user;

    public ServerFilesOperationHandler(ServerMessageHandler serverMessageHandler) {
        this.serverMessageHandler = serverMessageHandler;
    }

    public void processRequest(RequestMessage requestMessage) {
        if(this.user == null) {
            serverMessageHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_WRONG)));

        } else {
            FileOperationRequestType fileOperationRequestType = (FileOperationRequestType) requestMessage.getRequest().getRequestCommandType();
            switch (fileOperationRequestType) {
                case FILES_LIST:
                    serverFileService.getFileInfoListForView(requestMessage);
                    break;

                case SAVE_FILE:
                    serverFileService.saveFile(requestMessage);
                    break;

                case SAVE_DIRECTORY:
                    serverFileService.saveDirectory(requestMessage);
                    break;

                case DELETE_FILE:
                    serverFileService.deleteFile(requestMessage);
                    break;

                case DOWNLOAD_FILE:
                    serverFileService.downloadFile(requestMessage);
                    break;

                case DOWNLOAD_DIRECTORY:
                    serverFileService.downloadDirectory(requestMessage);
                    break;

                case FOLDER_UP:
                    serverFileService.folderUp(requestMessage);
                    break;
            }
        }
    }

    public void setServerFileService(User user) {
        this.user = user;
        this.serverFileService = Factory.getFileService(serverMessageHandler, user);
    }
}
