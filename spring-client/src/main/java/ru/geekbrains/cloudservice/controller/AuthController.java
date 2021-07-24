package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.service.AuthService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
@Component
@FxmlView("loginPage.fxml")
public class AuthController {

    private final FxWeaver fxWeaver;

    public AnchorPane mainDialog;

    private AuthService authService;

    private Stage stage;

    @Autowired
    public AuthController(FxWeaver fxWeaver, AuthService authService) {
        this.fxWeaver = fxWeaver;
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
        loginButton.getScene().getWindow().hide();
        String username = loginUserName.getText();
        String password = PasswordField.getText();

        authService.userLogin(username, password);

        while (!authService.isLoginConfirm()|| !authService.isLoginDecline()) {
            if (authService.isLoginConfirm()) {
                Optional<String> userPath = authService.findUserFolderPath();

                if(userPath.isPresent()) {
                    //если у юзера есть своя папка на устройстве -> переходим в папку
                    fxWeaver.loadController(MainController.class).show();
                    break;
                } else {
                    //если папки нет, предлагаем ему выбрать расположение папки на компьютере
                    fxWeaver.loadController(ModalPickFolder.class).show();
                    break;
                }
            }

            if(authService.isLoginDecline()) {
                //выводим лэйбл о том что неудачный вход
                break;
            }
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

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(mainDialog));
        stage.setMinWidth(650);
        stage.setMinHeight(400);
        stage.setResizable(false);
        stage.show();
    }


}

