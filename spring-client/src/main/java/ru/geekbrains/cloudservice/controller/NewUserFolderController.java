package ru.geekbrains.cloudservice.controller;

import javafx.application.Platform;
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
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.service.ClientAuthService;

import java.io.File;

@Slf4j
@Component
@FxmlView("modalPickFolder.fxml")
public class NewUserFolderController {

    private final FxWeaver fxWeaver;
    private final ClientAuthService clientAuthService;
    private final DataModel dataModel;

    @FXML
    private Stage stage;

    @FXML
    private AnchorPane modalWindowFolderChooser;

    @FXML
    private TextField pathTextField;

    @Autowired
    public NewUserFolderController(FxWeaver fxWeaver, ClientAuthService clientAuthService, DataModel dataModel) {
        this.fxWeaver = fxWeaver;
        this.clientAuthService = clientAuthService;
        this.dataModel = dataModel;
    }

    @FXML
    void closeModalWindow(ActionEvent event) {
        fxWeaver.loadController(AuthController.class).show();
        stage.getScene().getWindow().hide();
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
        Platform.runLater(() -> {
            this.stage = new Stage();
            stage.setScene(new Scene(modalWindowFolderChooser));
            stage.setMinWidth(600);
            stage.setMinHeight(177);
            stage.setResizable(false);
            stage.show();
        });
    }

    public void confirmUserFolderPath(ActionEvent actionEvent) {
        clientAuthService.createAndSetUserDirectory(pathTextField.getText());
        stage.getScene().getWindow().hide();
        fxWeaver.loadController(MainController.class).show();

    }
}