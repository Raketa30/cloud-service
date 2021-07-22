package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import ru.geekbrains.cloudservice.service.AuthService;

import java.io.File;
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
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configureDirectoryChooser(directoryChooser);
        folderChooserButton.setOnAction(actionEvent -> {
            File dir = directoryChooser.showDialog(null);
            if (dir != null) {
                folderPath.setText(dir.getAbsolutePath());
            } else {
                folderPath.setText(null);
            }
        });

        confirmRegButton.setOnAction(action -> {
            String username = userNameField.getText();
            String password = passwordField.getText();
            String passwordRepeat = passwordRepeatField.getText();
            if (username != null && password != null && password.equals(passwordRepeat)) {
                authService.registerUser(username, password);
            }
        });
    }

    private void configureDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select root folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }


}

