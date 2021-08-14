package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.service.ClientAuthService;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
@FxmlView("loginPage.fxml")
public class AuthController {
    private final FxWeaver fxWeaver;
    private final ClientAuthService clientAuthService;
    private final DataModel dataModel;

    @FXML
    public AnchorPane mainDialog;

    @FXML
    public Label wrongLogin;

    @FXML
    private Stage stage;

    @Autowired
    public AuthController(FxWeaver fxWeaver, ClientAuthService clientAuthService, DataModel dataModel) {
        this.fxWeaver = fxWeaver;
        this.clientAuthService = clientAuthService;
        this.dataModel = dataModel;
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
        String username = loginUserName.getText();
        String password = PasswordField.getText();
        if (validateCredentials(username, password)) {
            clientAuthService.userLogin(username, password);
            SimpleObjectProperty<UserTo> userProperty = dataModel.userProperty();
            userProperty.addListener((observable, oldValue, newValue) -> {
                if (!newValue.getUsername().equals("empty")) {
                    loginOk();
                } else {
                    wrongLogin.setVisible(true);
                }
            });
        }
    }

    //Читаем файл настроек и ищем папку юзера   юзер : папка
    @FXML
    void signUp(ActionEvent event) {
        loginButton.getScene().getWindow().hide();
        fxWeaver.loadController(RegistrationController.class).show();
    }

    @FXML
    void initialize() {

    }

    public void loginOk() {
        String path = dataModel.getRootPath();
        if (path.equals("empty")) {
            fxWeaver.loadController(NewFolderController.class).show();
        } else {
            fxWeaver.loadController(MainController.class).show();
        }
    }

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(mainDialog));
        stage.setMinWidth(650);
        stage.setMinHeight(400);
        stage.setResizable(false);
        stage.show();
    }

    private boolean validateCredentials(String username, String password) {
        return username != null && username.matches("[A-Za-z0-9]+")
                && !password.equals("");
    }
}

