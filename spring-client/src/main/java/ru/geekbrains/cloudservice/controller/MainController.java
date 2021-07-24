package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.service.AuthService;
import ru.geekbrains.cloudservice.service.FileService;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@FxmlView("mainView.fxml")
public class MainController {
    private final FxWeaver fxWeaver;

    private Path currentPath;

    @FXML
    public TextField pathField;
    //Сервисы
    private AuthService authService;
    private FileService fileService;

    @FXML
    private BorderPane mainDialog;

    @FXML
    private Stage stage;

    @FXML
    private TableView<FileInfo> filesList;

    @FXML
    private TableColumn<FileInfo, Long> fileSizeColumn;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label freeSpace;

    @FXML
    private JFXButton folderUpButton;

    @FXML
    private TableColumn<FileInfo, Circle> onAirColumn;

    @FXML
    private TableColumn<FileInfo, String> fileNameColumn;

    @FXML
    private TableColumn<FileInfo, String> fileTypeColumn;

    @FXML
    private TableColumn<FileInfo, JFXButton> uploadColumn;

    @FXML
    private TableColumn<FileInfo, String> fileLastModifiedColumn;

    @FXML
    private JFXListView<FileInfo> rootFoldersList;

    @Autowired
    public MainController(FxWeaver fxWeaver, AuthService authService, FileService fileService) {
        this.fxWeaver = fxWeaver;
        this.authService = authService;
        this.fileService = fileService;
    }

    @FXML
    private Circle connectionStatusLamp;

    @FXML
    void initialize() {
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getName()));
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getFileSize()));
        fileSizeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "";
                    }
                    setText(text);
                }
            }
        });

        fileLastModifiedColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()
                .getLastModified()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));

        filesList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                getCurrentPath();
                updateList(currentPath);
            }
        });
        currentPath = authService.getUserFolderPath();
        updateList(currentPath);
    }


    public void updateList(Path path) {
        Path relativizedPath = authService.getUserFolderPath().relativize(path);
        try {
            pathField.setText("/" + relativizedPath.normalize().toString());
            filesList.getItems().clear();
            filesList.getItems().addAll(
                    Files.list(path)
                            .map(FileInfo::new)
                            .collect(Collectors.toList())
            );
            filesList.sort();
        } catch (IOException e) {
            new Alert(Alert.AlertType.WARNING, "Не удалось отобразить список файлов", ButtonType.OK);
        }

    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        if(currentPath.equals(authService.getUserFolderPath())) {
            return;
        }
        Path upPath = currentPath.getParent();

        if (upPath != null) {
            currentPath = upPath;
            updateList(upPath);
        }
    }

    public String getSelectedFilename() {
        return filesList.getSelectionModel().getSelectedItem().getFilename();
    }

    public void getCurrentPath() {
        currentPath = currentPath.resolve(getSelectedFilename());
    }

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(mainDialog));
        stage.setTitle(authService.getUserTo().getUsername());
        stage.setResizable(false);
        connectionStatusLamp.setFill(Color.GREEN);
        stage.show();
    }
}

