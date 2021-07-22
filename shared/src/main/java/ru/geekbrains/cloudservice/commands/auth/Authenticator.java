package ru.geekbrains.cloudservice.commands.auth;

import ru.geekbrains.cloudservice.model.User;

public interface Authenticator {
   AuthRequestType getType();

   User getUserFromRequest();

}
