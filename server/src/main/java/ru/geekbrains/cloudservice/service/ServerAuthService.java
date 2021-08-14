package ru.geekbrains.cloudservice.service;

import io.netty.channel.ChannelHandlerContext;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAuthService {
    //Лист для залогиненых юзеров
    private final Map<ChannelHandlerContext, User> loggedUsers;
    private UserRepo userRepo;

    public ServerAuthService() {
        this.userRepo = new UserRepo();
        loggedUsers = new ConcurrentHashMap<>();
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
        user.setServerRootPath("folder-" + user.getUsername());
        try {
            Files.createDirectory(Paths.get("server/main_root_folder/" + user.getServerRootPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        userRepo.registerNewUser(user);
    }

    public void addLoggedUser(ChannelHandlerContext ctx, User user) {
        loggedUsers.put(ctx, user);
    }

    public void removeLoggedUser(ChannelHandlerContext ctx) {
        loggedUsers.remove(ctx);
    }

    public User getUserFormContext(ChannelHandlerContext ctx) {
        return loggedUsers.get(ctx);
    }
}
