package ru.geekbrains.cloudservice.commands.auth;

import ru.geekbrains.cloudservice.commands.CommandType;
import ru.geekbrains.cloudservice.commands.Request;

public class AuthRequest implements Request {
    private final CommandType commandType;

    public AuthRequest(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public CommandType getRequestCommandType() {
        return commandType;
    }

}
