package ru.geekbrains.cloudservice.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.geekbrains.cloudservice.configuration.MainConfiguration;
import ru.geekbrains.cloudservice.controller.AuthController;

public class MainClient extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainConfiguration mainConfiguration = new MainConfiguration(23232, "localhost");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/loginPage.fxml"));

        Parent root = loader.load();
        AuthController controller = loader.<AuthController>getController();
        controller.setAuthService(mainConfiguration.getAuthService());

        primaryStage.setTitle("version 1.0");
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(400);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.show();
    }
}
