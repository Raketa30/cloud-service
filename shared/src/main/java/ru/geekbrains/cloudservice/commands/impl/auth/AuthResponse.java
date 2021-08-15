package ru.geekbrains.cloudservice.commands.impl.auth;

import ru.geekbrains.cloudservice.commands.CommandType;
import ru.geekbrains.cloudservice.commands.Response;

public class AuthResponse implements Response {
    private final CommandType commandType;

    public AuthResponse(CommandType commandType) {
        this.commandType = commandType;
    }

    @Override
    public CommandType getResponseType() {
        return commandType;
    }
}
