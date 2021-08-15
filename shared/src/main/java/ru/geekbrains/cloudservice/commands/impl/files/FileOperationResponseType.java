package ru.geekbrains.cloudservice.commands.impl.files;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum FileOperationResponseType implements CommandType {
    FILE_SENT,
    DIRECTORY_SENT,

    FILE_LIST_SENT,

    FILE_NOT_EXIST,
    DIRECTORY_NOT_EXIST,

    FILE_SAVING_PROBLEM,
    EMPTY_LIST;
}
