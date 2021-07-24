package ru.geekbrains.cloudservice.repo;

import ru.geekbrains.cloudservice.dao.DBConnection;
import ru.geekbrains.cloudservice.model.User;

import java.util.Optional;


public class UserRepo {
    private DBConnection dbConnection;

    public UserRepo() {
        this.dbConnection = new DBConnection();
    }

    public Optional<User> findUserByLoginAndPassword(String username, String password) {
        User user = new User(username, password);
        return dbConnection.findUserByUsernameAndPassword(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return dbConnection.findUserByUsername(username);
    }

    public void registerNewUser(User user) {
        dbConnection.registerNewUser(user);
    }
}
