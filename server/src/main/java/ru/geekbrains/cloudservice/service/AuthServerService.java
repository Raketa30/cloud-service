package ru.geekbrains.cloudservice.service;

import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class AuthServerService {
    private UserRepo userRepo;

    public AuthServerService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> loginRequest(User user) {
        return userRepo.findUserByLoginAndPassword(user.getUsername(), user.getPassword());
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }


//    регистрируем нового пользователя и устанавливает ему рутовую папку на сервере

    public void registerNewUser(User user) {
        user.setActive(true);
        user.setServerRootPath("folder" + user.getUsername());
        try {
            Files.createDirectory(Paths.get("server/main_root_folder/" + user.getServerRootPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        userRepo.registerNewUser(user);
    }
}
