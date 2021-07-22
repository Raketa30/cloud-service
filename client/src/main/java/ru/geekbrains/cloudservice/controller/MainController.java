package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.shape.Circle;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController {

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
}

