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

    private AuthService service;

    public void setService(AuthService service) {
        this.service = service;
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
        loginButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/mainView.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String username = loginUserName.getText();
        String password = PasswordField.getText();

        service.userLogin(username, password);

        while (true) {
            if (service.isLogged()) {
                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(service.getUserTo().getUsername());
                stage.setMinWidth(900);
                stage.setMinHeight(650);
                stage.setResizable(false);
                stage.showAndWait();
                break;
            }
        }
    }

    @FXML
    void signUp(ActionEvent event) {
        loginButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/register.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setMinWidth(650);
        stage.setMinHeight(400);
        stage.setResizable(false);

        RegistrationController registrationController = loader.getController();
        registrationController.setAuthService(service);

        stage.showAndWait();
    }

    @FXML
    void initialize() {

    }
}

