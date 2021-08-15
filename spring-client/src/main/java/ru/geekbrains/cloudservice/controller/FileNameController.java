package ru.geekbrains.cloudservice.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.service.ClientFileService;

@Slf4j
@Component
@FxmlView("enterName.fxml")
public class FileNameController {

    private final FxWeaver fxWeaver;
    private final ClientFileService fileService;
    @FXML
    private AnchorPane folderName;

    @FXML
    private Stage stage;

    @FXML
    private TextField pathTextField;

    @Autowired
    public FileNameController(FxWeaver fxWeaver, ClientFileService fileService) {
        this.fxWeaver = fxWeaver;
        this.fileService = fileService;
    }

    @FXML
    void closeModalWindow(ActionEvent event) {
        stage.getScene().getWindow().hide();
    }

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(folderName));
        stage.setMinWidth(600);
        stage.setMinHeight(177);
        stage.setResizable(false);
        stage.show();
    }

    public void confirmUserFolderPath(ActionEvent actionEvent) {
        String folderName = pathTextField.getText().trim();
        if(!folderName.equals("")) {
            fileService.createNewFolder(folderName);
        }
        this.stage.hide();
    }
}
