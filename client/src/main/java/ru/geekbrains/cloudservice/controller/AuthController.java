package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.geekbrains.cloudservice.service.AuthService;

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
        });
    }
}

