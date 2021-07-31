package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum FileOperationRequestType  implements CommandType {
    SAVE_FILE,
    SAVE_FILE_REQUEST,
    DOWNLOAD_FILE,
    DOWNLOAD_FILE_LIST,
    FILES_LIST,
    SAVE_FOLDER;
}
