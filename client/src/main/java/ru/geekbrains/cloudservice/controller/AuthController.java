package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.geekbrains.cloudservice.service.AuthService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginUserName;

    @FXML
    private JFXButton regButton;

    @FXML
    private JFXButton loginButton;

    @FXML
    private PasswordField PasswordField;

    @FXML
    void signIn(ActionEvent event) {

    }

    @FXML
    void signUp(ActionEvent event) {

    }

    @FXML
    void initialize() {
        loginButton.setOnAction(actionEvent -> {
            String username = loginUserName.getText();
            String password = loginUserName.getText();
            authService.userLogin(username, password);

            if(true) {
                System.out.println(authService.isLogged());
                loginButton.getScene().getWindow().hide();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/mainView.fxml"));
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(authService.getUserTo().getUsername());
                stage.setMinWidth(900);
                stage.setMinHeight(650);
                stage.setResizable(false);
                stage.showAndWait();
            }
        });
    }
}

