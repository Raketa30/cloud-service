package ru.geekbrains.cloudservice.client.start;

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
        URL url = Paths.get("src/main/resources/fxml/register.fxml").toUri().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("version 1.0");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        primaryStage.setScene(new Scene(root, 900, 650));
        primaryStage.show();
    }
}
