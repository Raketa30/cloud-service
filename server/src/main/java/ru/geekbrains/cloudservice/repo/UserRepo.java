package ru.geekbrains.cloudservice.repo;

import ru.geekbrains.cloudservice.dao.UserDBConnection;
import ru.geekbrains.cloudservice.model.User;

import java.util.Optional;


public class UserRepo {
    private final UserDBConnection userDbConnection;

    public UserRepo() {
        this.userDbConnection = new UserDBConnection();
    }

    public Optional<User> findUserByLoginAndPassword(String username, String password) {
        User user = new User(username, password);
        return userDbConnection.findUserByUsernameAndPassword(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userDbConnection.findUserByUsername(username);
    }

    public void registerNewUser(User user) {
        userDbConnection.registerNewUser(user);
    }
}
