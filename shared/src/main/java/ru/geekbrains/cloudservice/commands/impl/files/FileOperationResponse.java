package ru.geekbrains.cloudservice.commands.impl.files;

import ru.geekbrains.cloudservice.commands.CommandType;
import ru.geekbrains.cloudservice.commands.Response;

public class FileOperationResponse implements Response {
    private final CommandType commandType;

    public FileOperationResponse(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public CommandType getResponseType() {
        return commandType;
    }
}
