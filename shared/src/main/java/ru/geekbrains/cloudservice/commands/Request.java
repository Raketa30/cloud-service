package ru.geekbrains.cloudservice.commands;

import ru.geekbrains.cloudservice.commands.auth.AuthRequestType;

public interface Request<T> {
   AuthRequestType getType();

   T getRequestBody();
}
