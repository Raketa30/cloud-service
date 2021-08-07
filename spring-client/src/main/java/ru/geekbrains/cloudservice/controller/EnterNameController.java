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
public class EnterNameController {

    private final ClientFileService clientFileService;
    private final MainController mainController;
    private final FxWeaver fxWeaver;
    public AnchorPane folderName;

    @FXML
    private Stage stage;

    @FXML
    private TextField pathTextField;

    @Autowired
    public EnterNameController(ClientFileService clientFileService, MainController mainController, FxWeaver fxWeaver) {
        this.clientFileService = clientFileService;
        this.mainController = mainController;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    void closeModalWindow(ActionEvent event) {
        fxWeaver.loadController(MainController.class).show();
        this.stage.hide();
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
        clientFileService.createNewFolder(pathTextField.getText(), mainController.getPath());
        this.stage.hide();
        mainController.updateList(mainController.getPath());
    }
}
