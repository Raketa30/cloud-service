package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum FileOperationRequestType  implements CommandType {
    SAVE_FILE,
    SAVE_DIRECTORY,
    SAVE_FILE_REQUEST,

    DELETE_FILE,

    DOWNLOAD_FILE,
    DOWNLOAD_DIRECTORY,

    FOLDER_UP,
    FILES_LIST;

}
