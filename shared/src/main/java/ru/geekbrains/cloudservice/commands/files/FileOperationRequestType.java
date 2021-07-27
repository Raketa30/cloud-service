package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum FileOperationRequestType  implements CommandType {
    SAVE_FILE,
    SAVE_FILE_REQUEST,
    SAVE_FILES_LIST_REQUEST,
    DOWNLOAD_FILE,
    DOWNLOAD_FILE_LIST,
    FILE_SENDED ;
}
