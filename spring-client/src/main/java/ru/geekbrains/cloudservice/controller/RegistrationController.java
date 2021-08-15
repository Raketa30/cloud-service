package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.service.ClientAuthService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
@FxmlView("register.fxml")
public class RegistrationController {
    private final ClientAuthService clientAuthService;
    private final DataModel dataModel;
    private final FxWeaver fxWeaver;
    @FXML
    public Label regWrong;

    @FXML
    private JFXButton backButton;

    @FXML
    private AnchorPane mainDialog;

    @FXML
    private Stage stage;

    @Autowired
    public RegistrationController(ClientAuthService clientAuthService, DataModel dataModel, FxWeaver fxWeaver) {
        this.clientAuthService = clientAuthService;
        this.dataModel = dataModel;
        this.fxWeaver = fxWeaver;
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
        this.stage = new Stage();
        stage.setScene(new Scene(mainDialog));
        stage.setMinWidth(650);
        stage.setMinHeight(400);
        stage.setResizable(false);
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

        if (validateCredentials(username, password, passwordRepeat)) {
            clientAuthService.registerUser(username, password);
            SimpleObjectProperty<UserTo> userProperty = dataModel.registeredUserProperty();
            userProperty.addListener((observable, oldValue, newValue) -> {
                if (!newValue.getUsername().equals("*empty")) {
                    clientAuthService.createLocalUserDirectory(folderPath.getText(), username);
                    dataModel.setRegisteredUser(new UserTo("*empty"));
                    registerOk();
                } else {
                    regWrong.setVisible(true);
                }
            });
        }
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

    private boolean validateCredentials(String username, String password, String passwordRepeat) {
        return username != null && username.matches("[A-Za-z0-9]+")
                && !password.equals("")
                && password.equals(passwordRepeat);
    }

    private void registerOk() {
        Platform.runLater(() -> {
            confirmRegButton.getScene().getWindow().hide();
            fxWeaver.loadController(AuthController.class).show();
        });
    }

    public void show() {
        stage.show();
    }

    public void backToPreviousStage(ActionEvent actionEvent) {
        backButton.getScene().getWindow().hide();
        fxWeaver.loadController(AuthController.class).show();
    }
}

