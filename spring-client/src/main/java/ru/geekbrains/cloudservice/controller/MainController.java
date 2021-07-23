package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("mainView.fxml")
public class MainController {
    private final FxWeaver fxWeaver;
    private String userRootPath;

    @Autowired
    public MainController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    public String getUserRootPath() {
        return userRootPath;
    }

    public void setUserRootPath(String userRootPath) {
        this.userRootPath = userRootPath;
        stage.setTitle(userRootPath);
    }

    @FXML
    public BorderPane mainDialog;

    private Stage stage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label freeSpace;

    @FXML
    private JFXButton folderUpColumn;

    @FXML
    private TableView<String> filesList;

    @FXML
    private TableColumn<FileInfo, String> fileNameColumn;

    @FXML
    private TableColumn<FileInfo, String> fileTypeColumn;

    @FXML
    private TableColumn<FileInfo, String> fileSizeColumn;

    @FXML
    private TableColumn<FileInfo, String> fileLastModifiedColumn;

    @FXML
    private TableColumn<FileInfo, ?> onAirColumn;

    @FXML
    private TableColumn<?, ?> uploadColumn;

    @FXML
    private Circle connectionStatusLamp;

    @FXML
    private JFXListView<?> rootFoldersList;

    @FXML
    void initialize() {

    }

    public void show(UserTo userTo) {
        this.stage = new Stage();
        stage.setScene(new Scene(mainDialog));
        stage.setTitle(userTo.getUsername() );
        stage.setResizable(false);
        stage.show();
        if(userRootPath == null) {
            fxWeaver.loadController(ModalPickFolder.class).show();
        }
    }
}

