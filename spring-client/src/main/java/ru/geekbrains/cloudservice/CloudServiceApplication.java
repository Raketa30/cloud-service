package ru.geekbrains.cloudservice;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.geekbrains.cloudservice.start.MainClient;

@SpringBootApplication
public class CloudServiceApplication {

    public static void main(String[] args) {
        Application.launch(MainClient.class, args);
    }

}
