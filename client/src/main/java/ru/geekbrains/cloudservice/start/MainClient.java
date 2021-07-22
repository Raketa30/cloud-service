package ru.geekbrains.cloudservice.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Paths;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = Paths.get("src/main/resources/fxml/login.fxml").toUri().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("version 1.0");
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(400);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.show();
    }
}
