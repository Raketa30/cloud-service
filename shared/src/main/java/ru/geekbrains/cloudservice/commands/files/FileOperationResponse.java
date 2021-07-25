package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.io.Serializable;

public class FileOperationResponse implements Response<FileInfo, FilesOperationResponseType>, Serializable {
    private final FilesOperationResponseType responseType;
    private final FileInfo fileInfo;

    public FileOperationResponse(FilesOperationResponseType responseType, FileInfo fileInfo) {
        this.responseType = responseType;
        this.fileInfo = fileInfo;
    }

    @Override
    public FilesOperationResponseType getResponseType() {
        return this.responseType;
    }

    @Override
    public FileInfo getResponseBody() {
        return this.fileInfo;
    }
}
