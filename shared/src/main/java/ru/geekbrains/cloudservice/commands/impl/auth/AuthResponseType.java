package ru.geekbrains.cloudservice.commands.impl.auth;

import ru.geekbrains.cloudservice.commands.CommandType;

public enum AuthResponseType implements CommandType {
    LOGIN_OK,
    LOGIN_WRONG,
    REGISTRATION_OK,
    REGISTRATION_WRONG_USER_EXIST
}
