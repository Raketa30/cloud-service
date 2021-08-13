package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.FileServerService;

@Slf4j
public class ServerFilesOperationHandler {
    private final FileServerService fileServerService;
    private final ServerMessageHandler serverMessageHandler;

    public ServerFilesOperationHandler(ServerMessageHandler serverMessageHandler) {

        fileServerService = new FileServerService(serverMessageHandler);
        this.serverMessageHandler = serverMessageHandler;
    }

    public void processRequest(RequestMessage requestMessage,User activeUser) {
        log.info("received file request {}", requestMessage.getRequest());
        if(activeUser == null) {
            serverMessageHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_WRONG)));

        } else {
            fileServerService.setUserFolderPath(activeUser);
            FileOperationRequestType fileOperationRequestType = (FileOperationRequestType) requestMessage.getRequest().getRequestCommandType();

            switch (fileOperationRequestType) {
                case FILES_LIST:
                    fileServerService.getFileInfoListForView(requestMessage);
                    break;

                case SAVE_FILE_REQUEST:
                    fileServerService.checkReceivedFileInfo(requestMessage);
                    break;

                case SAVE_FILE:
                    fileServerService.saveFile(requestMessage);
                    break;

                case DOWNLOAD_FILE:
                    fileServerService.downloadFile(requestMessage);
                    break;

                case DOWNLOAD_FILE_LIST:
                    break;
            }
        }
    }
}
