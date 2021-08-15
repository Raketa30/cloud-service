package ru.geekbrains.cloudservice.commands.impl.auth;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum AuthRequestType implements CommandType {
    LOGIN,
    REGISTRATION,
    LOGOUT
}
