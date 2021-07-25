package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.io.Serializable;

public class FilesOperationRequest implements Request<FileInfo, FileOperationRequestType>, Serializable {
    private final FileOperationRequestType requestType;
    private final FileInfo fileInfo;

    public FilesOperationRequest(FileOperationRequestType requestType, FileInfo fileInfo) {
        this.requestType = requestType;
        this.fileInfo = fileInfo;
    }

    @Override
    public FileOperationRequestType getType() {
        return this.requestType;
    }

    @Override
    public FileInfo getRequestBody() {
        return this.fileInfo;
    }
}
