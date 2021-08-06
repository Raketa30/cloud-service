package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationResponseType;
import ru.geekbrains.cloudservice.dto.FileInfoTo;
import ru.geekbrains.cloudservice.service.ClientFileService;
import ru.geekbrains.cloudservice.service.FileListViewService;

@Service
@Slf4j
public class FilesOperationResponseHandler {
    private final ClientFileService clientFileService;
    private final FileListViewService fileListViewService;

    @Autowired
    public FilesOperationResponseHandler(ClientFileService clientFileService, FileListViewService fileListViewService) {
        this.clientFileService = clientFileService;
        this.fileListViewService = fileListViewService;
    }

    public void processHandler(ResponseMessage responseMessage) {
        FileOperationResponseType fileOperationRequestType = (FileOperationResponseType) responseMessage.getResponse().getResponseType();

        switch (fileOperationRequestType) {
            case FILE_READY_TO_SAVE:
                clientFileService.sendFileToServer(responseMessage);
                break;

            case DIRECTORY_READY_TO_SAVE:
                clientFileService.sendDirectoryToServer(responseMessage);
                break;

            case FILE_ALREADY_EXIST:
                FileInfoTo fileInfoTo = (FileInfoTo) responseMessage.getAbstractMessageObject();
                log.debug("File already exist {}", fileInfoTo);
                break;

            case FILE_LIST_SENT:
                fileListViewService.addFileListFromServer(responseMessage);
                break;

            case FILE_SENT:
                clientFileService.saveFileFromServer(responseMessage);
                break;

            case EMPTY_LIST:
                log.debug("server file list empty");
                break;

            case FILE_NOT_EXIST:
                break;

            case DIRECTORY_NOT_EXIST:
                break;
        }
    }


}
