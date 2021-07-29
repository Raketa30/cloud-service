package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.service.ClientAuthService;
import ru.geekbrains.cloudservice.service.ClientFileService;

@Service
@Slf4j
public class FilesOperationResponseHandler {
    private final ClientFileService clientFileService;
    private final ClientAuthService clientAuthService;

    @Autowired
    public FilesOperationResponseHandler(ClientFileService clientFileService, ClientAuthService clientAuthService) {
        this.clientFileService = clientFileService;
        this.clientAuthService = clientAuthService;
    }

    public void processHandler(ResponseMessage responseMessage) {
        FilesOperationResponseType fileOperationRequestType = (FilesOperationResponseType) responseMessage.getResponse().getResponseType();

        switch (fileOperationRequestType) {
            case FILE_READY_TO_SAVE:
                FileInfo responseBody = (FileInfo) responseMessage.getAbstractMessageObject();
                clientFileService.sendFileToServer(responseBody);
                break;
            case FILE_ALREADY_EXIST:
                FileInfo fileInfo = (FileInfo) responseMessage.getAbstractMessageObject();
                log.debug("File allraedy exist {}",fileInfo);
                break;
            case FILE_SENT:
                break;
            case FILE_LIST_SENT:
                break;
            case FILE_NOT_EXIST:
                break;
            case DIRECTORY_NOT_EXIST:
                break;
        }
    }
}
