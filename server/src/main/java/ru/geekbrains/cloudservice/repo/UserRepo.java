package ru.geekbrains.cloudservice.repo;

import ru.geekbrains.cloudservice.dao.DBConnection;
import ru.geekbrains.cloudservice.model.User;


public class UserRepo {
    private DBConnection dbConnection;

    public UserRepo(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public User findUserByLoginAndPassword(String username, String password) {
        return null;
    }
}
