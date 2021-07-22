package ru.geekbrains.cloudservice.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//        new MainConfiguration(8189, "localhost");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("version 1.0");
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(400);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.show();
    }
}
