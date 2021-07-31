package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum FileOperationResponseType implements CommandType {
    FILE_ALREADY_EXIST,
    FILE_READY_TO_SAVE,
    DIRECTORY_READY_TO_SAVE,

    FILE_LIST_SENT,

    FILE_NOT_EXIST,
    DIRECTORY_NOT_EXIST,

    FILE_SAVING_PROBLEM,
    EMPTY_LIST;
}
