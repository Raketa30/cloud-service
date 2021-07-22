package ru.geekbrains.cloudservice.service;

import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserRepo;

public class AuthServerService {
    private UserRepo userRepo;

    public AuthServerService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User loginRequest(User user) {
        return userRepo.findUserByLoginAndPassword(user.getUsername(), user.getPassword());
    }
}
