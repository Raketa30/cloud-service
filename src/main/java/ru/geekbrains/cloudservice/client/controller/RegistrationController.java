package ru.geekbrains.cloudservice.client.controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class RegistrationController {

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
    }

    private void configureDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select root folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }


}

