package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum FileOperationRequestType  implements CommandType {
    SAVE_FILE,
    SAVE_FILE_REQUEST,

    SAVE_DIRECTORY,

    DELETE_FILE,

    DOWNLOAD_FILE,
    DOWNLOAD_FILE_LIST,

    FILES_LIST;

}
