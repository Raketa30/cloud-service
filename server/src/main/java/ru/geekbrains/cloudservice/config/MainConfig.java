package ru.geekbrains.cloudservice.config;

import ru.geekbrains.cloudservice.dao.DBConnection;
import ru.geekbrains.cloudservice.repo.UserRepo;
import ru.geekbrains.cloudservice.service.AuthServerService;

public class MainConfig {
    private AuthServerService authServerService;
    private DBConnection dbConnection;
    private UserRepo userRepo;

    public MainConfig() {
        initRepo();
        initServices();
    }

    private void initServices() {
        authServerService = new AuthServerService(userRepo);
    }

    private void initRepo() {
        dbConnection = new DBConnection();
        userRepo = new UserRepo(dbConnection);
    }
}
