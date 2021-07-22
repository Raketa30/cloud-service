package ru.geekbrains.cloudservice.service;

import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserRepo;

import java.util.Optional;

public class AuthServerService {
    private UserRepo userRepo;

    public AuthServerService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> loginRequest(User user) {
        return userRepo.findUserByLoginAndPassword(user.getUsername(), user.getPassword());
    }

    public boolean findUserByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }

    public void registerNewUser(User user) {
        user.setActive(true);
        user.setServerRootPath("folder" + user.getUsername());
        userRepo.registerNewUser(user);
    }
}
