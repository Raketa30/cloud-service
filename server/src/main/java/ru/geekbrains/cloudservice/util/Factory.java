package ru.geekbrains.cloudservice.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Factory {
    private Factory() {

    }

    public static Path getServerFolderPath() {
        return Paths.get("/Users/duckpool/dev/courses/Geekbrains/cloud-service/server/main_root_folder/");
    }
}
