package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.model.FileInfo;

public class FilesOperationRequest implements Request<FileInfo, FileOperationRequestType> {

    public FilesOperationRequest() {
    }

    @Override
    public FileOperationRequestType getType() {
        return null;
    }

    @Override
    public FileInfo getRequestBody() {
        return null;
    }
}
