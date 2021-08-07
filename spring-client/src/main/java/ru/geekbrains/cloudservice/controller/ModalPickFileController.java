package ru.geekbrains.cloudservice.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.service.ClientFileService;

import java.io.File;
import java.nio.file.Paths;

@Slf4j
@Component
@FxmlView("modalAddFile.fxml")
public class ModalPickFileController {

    private final ClientFileService clientFileService;
    private final MainController mainController;
    private final FxWeaver fxWeaver;

    @FXML
    private Stage stage;

    @FXML
    private AnchorPane modalWindowFileChooser;

    @FXML
    private TextField pathTextField;

    @Autowired
    public ModalPickFileController(ClientFileService clientFileService, MainController mainController, FxWeaver fxWeaver) {
        this.clientFileService = clientFileService;
        this.mainController = mainController;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    void closeModalWindow(ActionEvent event) {
        fxWeaver.loadController(MainController.class).show();
        this.stage.hide();
    }

    @FXML
    void pickChoosedPath(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        configureDirectoryChooser(fileChooser);

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            pathTextField.setText(file.getAbsolutePath());
        } else {
            pathTextField.setText(null);
        }
    }

    private void configureDirectoryChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Select root folder");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(modalWindowFileChooser));
        stage.setMinWidth(600);
        stage.setMinHeight(177);
        stage.setResizable(false);
        stage.show();
    }

    public void confirmUserFolderPath(ActionEvent actionEvent) {
        clientFileService.copyFileToUserFolder(Paths.get(pathTextField.getText()), mainController.getPath());
        this.stage.hide();
        mainController.updateList(mainController.getPath());
    }
}
