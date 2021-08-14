package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponseType;
import ru.geekbrains.cloudservice.service.ClientFileService;
import ru.geekbrains.cloudservice.service.ClientFilesOperationService;

@Service
@Slf4j
public class FilesOperationResponseHandler {
    private final ClientFileService clientFileService;
    private final ClientFilesOperationService clientFilesOperationService;

    @Autowired
    public FilesOperationResponseHandler(ClientFileService clientFileService, ClientFilesOperationService clientFilesOperationService) {
        this.clientFileService = clientFileService;
        this.clientFilesOperationService = clientFilesOperationService;
    }

    public void processHandler(ResponseMessage responseMessage) {
        FileOperationResponseType fileOperationRequestType = (FileOperationResponseType) responseMessage.getResponse().getResponseType();

        switch (fileOperationRequestType) {
            case FILE_LIST_SENT:
                clientFilesOperationService.addFileListFromServer(responseMessage);
                break;

            case FILE_SENT:
                clientFileService.saveFileFromServer(responseMessage);
                break;

            case DIRECTORY_SENT:
                clientFileService.saveDirectory(responseMessage);
                break;

            case EMPTY_LIST:
                log.debug("server file list empty");
                break;

            case FILE_NOT_EXIST:
                clientFileService.updateLocalList(responseMessage);
                break;

            case DIRECTORY_NOT_EXIST:
                break;
        }
    }


}
