package ru.geekbrains.cloudservice.serial;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LocalUserPath implements Serializable {
    private final Map<String, Path> paths;

    public LocalUserPath() {
        this.paths = new HashMap<>();
    }

    public void addNewUserPath(String username, Path userFolder) {
        paths.put(username, userFolder);
    }

    public Optional<Path> getUserPath(String username) {
        return Optional.of(paths.get(username));
    }
}
