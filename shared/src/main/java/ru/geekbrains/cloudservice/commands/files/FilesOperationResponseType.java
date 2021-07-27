package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum FilesOperationResponseType implements CommandType {
    FILE_ALREADY_EXIST,
    FILE_READY_TO_SAVE,

    FILE_SAVED,
    FILES_LIST_SAVED,

    FILE_SENT,
    FILE_LIST_SENT,

    FILE_NOT_EXIST,
    DIRECTORY_NOT_EXIST
}
