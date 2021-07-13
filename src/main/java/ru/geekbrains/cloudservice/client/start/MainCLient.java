package ru.geekbrains.cloudservice.client.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainCLient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("version 1.0");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);

        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


}