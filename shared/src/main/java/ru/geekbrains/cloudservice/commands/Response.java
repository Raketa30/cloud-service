package ru.geekbrains.cloudservice.commands;

import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;

public interface Response<T> {
    AuthResponseType getResponseType();

    T getResponseBody();
}
