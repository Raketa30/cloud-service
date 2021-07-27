package ru.geekbrains.cloudservice.commands.files;

import ru.geekbrains.cloudservice.commands.CommandType;
import ru.geekbrains.cloudservice.commands.Request;

public class FileOperationRequest implements Request {
    private final CommandType commandType;

    public FileOperationRequest(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public CommandType getRequestCommandType() {
        return commandType;
    }
}
