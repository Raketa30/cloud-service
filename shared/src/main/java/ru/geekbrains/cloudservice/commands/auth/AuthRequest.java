package ru.geekbrains.cloudservice.commands.auth;

import lombok.Getter;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.model.User;

import java.io.Serializable;


@Getter
public class AuthRequest implements Request<User>, Serializable {
    private final String username;
    private final String password;
    private final AuthRequestType type;

    public AuthRequest(String username, String password, AuthRequestType type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    @Override
    public AuthRequestType getType() {
        return this.type;

    }

    @Override
    public User getRequestBody() {
        return new User(username, password);
    }

}
