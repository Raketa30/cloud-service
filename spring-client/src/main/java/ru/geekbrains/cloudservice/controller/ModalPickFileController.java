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
import ru.geekbrains.cloudservice.service.ClientFilesOperationService;

import java.io.File;

@Slf4j
@Component
@FxmlView("modalAddFile.fxml")
public class ModalPickFileController {

    private final ClientFilesOperationService operationService;
    private final ClientFileService fileService;
    private final FxWeaver fxWeaver;

    @FXML
    private Stage stage;

    @FXML
    private AnchorPane modalWindowFileChooser;

    @FXML
    private TextField pathTextField;

    @Autowired
    public ModalPickFileController(ClientFilesOperationService operationService, ClientFileService fileService, FxWeaver fxWeaver) {
        this.operationService = operationService;
        this.fileService = fileService;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    void closeModalWindow(ActionEvent event) {
        stage.getScene().getWindow().hide();
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
        fileService.addNewFile(pathTextField.getText());
        this.stage.hide();
    }
}
