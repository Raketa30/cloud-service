package ru.geekbrains.cloudservice.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.service.FileService;
@Service
public class FilesOperationResponseHandler {
    private final FileService fileService;

    @Autowired
    public FilesOperationResponseHandler(FileService fileService) {
        this.fileService = fileService;
    }

    public void processHandler(ResponseMessage responseMessage) {
        FilesOperationResponseType fileOperationRequestType = (FilesOperationResponseType) responseMessage.getResponse().getResponseType();

        switch (fileOperationRequestType) {
            case FILE_READY_TO_SAVE:
                FileInfo responseBody = (FileInfo) responseMessage.getAbstractMessageObject();
                fileService.sendRequestForFileSaving(responseBody);
                break;
            case FILE_ALREADY_EXIST:
                break;
            case FILE_SAVED:
                break;
            case FILES_LIST_SAVED:
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
