package ru.geekbrains.cloudservice.commands.files;

public enum FilesOperationResponseType {
    FILE_ALREADY_EXIST,
    FILE_READY_TO_SAVE,

    FILE_SAVED,
    FILES_LIST_SAVED,

    FILE_SENT,
    FILE_LIST_SENT,

    FILE_NOT_EXIST,
    DIRECTORY_NOT_EXIST
}
