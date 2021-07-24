package ru.geekbrains.cloudservice.service;

import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthServerService {
    //Лист для залогиненых юзеров
    private final List<User> loggedUsers;
    private UserRepo userRepo;

    public AuthServerService() {
        this.userRepo = new UserRepo();
        loggedUsers = new ArrayList<>();
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

    public List<User> getLoggedUsers() {
        return loggedUsers;
    }
}
