package ru.geekbrains.cloudservice.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.geekbrains.cloudservice.api.NettyConnector;
import ru.geekbrains.cloudservice.controller.AuthController;

public class MainClient extends Application{
    AuthController authController;
    private NettyConnector nettyConnector;
    private FXMLLoader loader;

    public MainClient() {
        System.out.println("constr");
        nettyConnector = new NettyConnector("localhost", 23232);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("start");
        loader = new FXMLLoader(getClass().getResource("/fxml/loginPage.fxml"));

        Parent root = loader.load();
        primaryStage.setTitle("version 1.0");
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(400);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 650, 400));

        authController = loader.getController();
        authController.setService(nettyConnector.getAuthService());

        primaryStage.show();

    }
}
