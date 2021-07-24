package ru.geekbrains.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.api.FileHandler;

@Slf4j
@Component
public class FileService {
    private FileHandler fileHandler;

    @Autowired
    public FileService(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }
}
