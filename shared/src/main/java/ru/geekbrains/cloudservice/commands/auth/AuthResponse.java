package ru.geekbrains.cloudservice.commands.auth;

import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.User;

import java.io.Serializable;

public class AuthResponse implements Response<UserTo>, Serializable {
    private final AuthResponseType authResponseType;
    private UserTo userTo;

    public AuthResponse(AuthResponseType authResponseType, User user) {
        this.authResponseType = authResponseType;
        userTo = new UserTo(user.getUsername());
    }

    public AuthResponse(AuthResponseType authResponseType) {
        this.authResponseType = authResponseType;
    }

    @Override
    public AuthResponseType getResponseType() {
        return this.authResponseType;
    }

    @Override
    public UserTo getResponseBody() {
        return userTo;
    }


}
