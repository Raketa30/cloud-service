package ru.geekbrains.cloudservice.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.service.AuthService;

import java.io.File;

@Slf4j
@Component
@FxmlView("modalPickFolder.fxml")
public class ModalPickFolder {

    private final FxWeaver fxWeaver;
    private final AuthService authService;

    @FXML
    private Stage stage;

    @FXML
    private AnchorPane modalWindowFolderChooser;

    @FXML
    private TextField pathTextField;

    @Autowired
    public ModalPickFolder(FxWeaver fxWeaver, AuthService authService) {
        this.fxWeaver = fxWeaver;
        this.authService = authService;
    }

    @FXML
    void closeModalWindow(ActionEvent event) {
        authService.resetFlags();
        fxWeaver.loadController(AuthController.class).show();
        this.stage.hide();
    }

    @FXML
    void pickChoosedPath(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configureDirectoryChooser(directoryChooser);

        File dir = directoryChooser.showDialog(null);
        if (dir != null) {
            pathTextField.setText(dir.getAbsolutePath());
        } else {
            pathTextField.setText(null);
        }
    }

    private void configureDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select root folder");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(modalWindowFolderChooser));
        stage.setMinWidth(600);
        stage.setMinHeight(177);
        stage.setResizable(false);
        stage.show();
    }

    public void confirmUserFolderPath(ActionEvent actionEvent) {
        authService.createLocalUserDirectory(pathTextField.getText());
        fxWeaver.loadController(MainController.class).show();
        this.stage.hide();
    }
}