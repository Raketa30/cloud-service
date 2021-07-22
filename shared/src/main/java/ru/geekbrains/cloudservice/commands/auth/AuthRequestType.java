package ru.geekbrains.cloudservice.commands.auth;

public enum AuthRequestType {
    LOGIN,
    REGISTRATION,
    LOGIN_OK,
    LOGIN_WRONG,
    REGISTRATION_OK,
    REGISTRATION_WRONG_USER_EXIST
}
