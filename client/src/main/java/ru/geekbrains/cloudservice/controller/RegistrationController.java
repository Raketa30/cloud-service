package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ru.geekbrains.cloudservice.service.AuthService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class RegistrationController {
    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField folderPath;

    @FXML
    private JFXButton folderChooserButton;

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordRepeatField;

    @FXML
    private JFXButton confirmRegButton;

    @FXML
    public void initialize() {

    }

    private void configureDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select root folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    @FXML
    public void registerNewUSer(ActionEvent actionEvent) {
        String username = userNameField.getText();
        String password = passwordField.getText();
        String passwordRepeat = passwordRepeatField.getText();
        if (username != null && password != null && password.equals(passwordRepeat)) {
            authService.registerUser(username, password);
        }

        confirmRegButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login.fxml"));

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

        stage.showAndWait();
    }

    public void chooseFolder(ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configureDirectoryChooser(directoryChooser);

        File dir = directoryChooser.showDialog(null);
        if (dir != null) {
            folderPath.setText(dir.getAbsolutePath());
        } else {
            folderPath.setText(null);
        }
    }
}

