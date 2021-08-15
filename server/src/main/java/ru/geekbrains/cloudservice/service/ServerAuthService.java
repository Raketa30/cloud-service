package ru.geekbrains.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.impl.RequestMessage;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.repo.UserRepo;
import ru.geekbrains.cloudservice.util.Factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ServerAuthService {
    private final UserRepo userRepo;
    private final List<User> loggedUser;

    public ServerAuthService() {
        this.userRepo = Factory.getUserRepo();
        this.loggedUser = new CopyOnWriteArrayList<>();
    }

    public Optional<User> loginRequest(User user) {
        return userRepo.findUserByLoginAndPassword(user.getUsername(), user.getPassword());
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }

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

    public void removeLoggedUser() {
    }

    public Optional<User> loginUser(RequestMessage requestMessage) {
        User tempUser = (User) requestMessage.getAbstractMessageObject();
        Optional<User> optionalUser = loginRequest(tempUser);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(!loggedUser.contains(user)) {
                loggedUser.add(user);
                log.info("user {} successfully logged", user);
                return Optional.of(user);
            }
            log.info("user {} already logged", user);
        }
        log.info("wrong login attempt");
        return Optional.empty();
    }

    public Optional<User> registerUser(RequestMessage requestMessage) {
        User user = (User) requestMessage.getAbstractMessageObject();
        Optional<User> regUser = findUserByUsername(user.getUsername());
        if (regUser.isEmpty()) {
            registerNewUser(user);
            log.info(user.toString());
            return Optional.of(user);
        }
        log.info("wrong registration attempt, user exist");
        return Optional.empty();
    }
}
