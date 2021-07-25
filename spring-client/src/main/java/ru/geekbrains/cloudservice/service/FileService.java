package ru.geekbrains.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.FileHandler;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.commands.files.FilesOperationRequest;
import ru.geekbrains.cloudservice.model.FileInfo;

@Slf4j
@Service
public class FileService {
    @Autowired
    private FileHandler fileHandler;

    public void sendRequestForFileSaving(FileInfo fileInfo) {
        fileHandler.sendFileOperationRequest(new FilesOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST, fileInfo));
        log.info("sendRequestForFileSaving {}", fileInfo);
    }


}
