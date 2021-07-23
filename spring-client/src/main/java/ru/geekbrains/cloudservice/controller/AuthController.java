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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

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

        while (true) {
            if (authService.isLoginConfirm()) {
                Optional<String> userPath = findUserFolderPath(authService.getUserTo().getUsername());

                if(userPath.isPresent()) {
                    fxWeaver.loadController(MainController.class).setUserRootPath(userPath.get());
                    fxWeaver.loadController(MainController.class).show(authService.getUserTo());
                    break;
                } else {
                    fxWeaver.loadController(MainController.class).show(authService.getUserTo());
                    break;
                }

            }
        }
    }

    //Читаем файл настроек и ищем папку юзера   юзер : папка
    private Optional<String> findUserFolderPath(String username) {
        try (Scanner scanner = new Scanner(new File("spring-client/settings.txt"))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] credentials = line.split(" : ");

                if (credentials[0].equals(username)) {
                    return Optional.of(credentials[1]);
                }
            }
        } catch (IOException e) {
            log.warn("File setting not found");
        }
        return Optional.empty();

    }

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

