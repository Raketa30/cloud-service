package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponseType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.model.FilesList;
import ru.geekbrains.cloudservice.service.ClientFileService;

@Service
@Slf4j
public class FilesOperationResponseHandler {
    private final ClientFileService clientFileService;

    @Autowired
    public FilesOperationResponseHandler(ClientFileService clientFileService) {
        this.clientFileService = clientFileService;
    }

    public void processHandler(ResponseMessage responseMessage) {
        FileOperationResponseType fileOperationRequestType = (FileOperationResponseType) responseMessage.getResponse().getResponseType();

        switch (fileOperationRequestType) {
            case FILE_READY_TO_SAVE:
                FileInfoTo responseBody = (FileInfoTo) responseMessage.getAbstractMessageObject();
                clientFileService.sendFileToServer(responseBody);
                break;

            case FILE_ALREADY_EXIST:
                FileInfoTo fileInfoTo = (FileInfoTo) responseMessage.getAbstractMessageObject();
                log.debug("File allraedy exist {}", fileInfoTo);
                break;

            case FILE_SAVED:
                break;
            case FILE_LIST_SENT:
                FilesList listInfo = (FilesList) responseMessage.getAbstractMessageObject();
                clientFileService.addFileListToView(listInfo);
                break;

            case EMPTY_LIST:
                clientFileService.addLocalFilesToView();
                break;

            case FILE_NOT_EXIST:
                break;
            case DIRECTORY_NOT_EXIST:
                break;
        }
    }


}
