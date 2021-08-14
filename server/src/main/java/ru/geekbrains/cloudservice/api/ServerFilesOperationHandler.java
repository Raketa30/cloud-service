package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthResponse;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.ServerFileService;

@Slf4j
public class ServerFilesOperationHandler {
    private final ServerFileService serverFileService;
    private final ServerMessageHandler serverMessageHandler;

    public ServerFilesOperationHandler(ServerMessageHandler serverMessageHandler) {

        serverFileService = new ServerFileService(serverMessageHandler);
        this.serverMessageHandler = serverMessageHandler;
    }

    public void processRequest(RequestMessage requestMessage,User activeUser) {
        log.info("received file request {}", requestMessage.getRequest());
        if(activeUser == null) {
            serverMessageHandler.sendResponse(new ResponseMessage(new AuthResponse(AuthResponseType.LOGIN_WRONG)));

        } else {
            serverFileService.setUserFolderPath(activeUser);
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
}
