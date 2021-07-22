package ru.geekbrains.cloudservice.commands.auth;

import lombok.Getter;
import ru.geekbrains.cloudservice.model.User;

import java.io.Serializable;


@Getter
public class AuthRequest implements Authenticator, Serializable {
    private final String username;
    private final String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public AuthRequestType getType() {
        return AuthRequestType.LOGIN;

    }

    @Override
    public User getUserFromRequest() {
        return new User(this.username, this.password);
    }
}
