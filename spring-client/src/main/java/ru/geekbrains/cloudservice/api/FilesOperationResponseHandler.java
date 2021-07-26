package ru.geekbrains.cloudservice.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.Response;
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

    public void processHandler(Response<FileInfo, FilesOperationResponseType> response) {
        switch (response.getResponseType()) {
            case FILE_READY_TO_SAVE:
                FileInfo responseBody = response.getResponseBody();
                fileService.sendFileToServer(responseBody);
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
