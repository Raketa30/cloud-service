package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.model.DataModel;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.UploadedStatus;
import ru.geekbrains.cloudservice.service.ClientFileService;

import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Component
@FxmlView("mainView.fxml")
public class MainController {
    private final FxWeaver fxWeaver;

    @FXML
    public TextField pathField;
    //Сервисы
    private final ClientFileService clientFileService;
    private final DataModel dataModel;

    @FXML
    private BorderPane mainDialog;

    @FXML
    private Stage stage;

    @FXML
    private TableView<FileInfo> filesList;

    @FXML
    private TableColumn<FileInfo, Long> fileSizeColumn;

    @FXML
    private TableColumn<FileInfo, String> fileNameColumn;

    @FXML
    private TableColumn<FileInfo, String> fileTypeColumn;

    @FXML
    public TableColumn<FileInfo, UploadedStatus> deleteColumn;

    @FXML
    private TableColumn<FileInfo, UploadedStatus> onAirColumn;

    @FXML
    private TableColumn<FileInfo, UploadedStatus> upDownColumn;

    @FXML
    private TableColumn<FileInfo, String> fileLastModifiedColumn;

    private String currentRelativePath;

    @Autowired
    public MainController(FxWeaver fxWeaver, ClientFileService clientFileService, DataModel dataModel) {
        this.fxWeaver = fxWeaver;
        this.clientFileService = clientFileService;
        this.dataModel = dataModel;
    }

    @FXML
    private Circle connectionStatusLamp;

    @FXML
    void initialize() {
        SimpleStringProperty relative = dataModel.relativePathProperty();
        currentRelativePath = relative.getValue();
        pathField.setText(relative.getValue());
        relative.addListener((observable, oldValue, newValue) -> {
            currentRelativePath = newValue;
            pathField.setText("/" + newValue);
        });

        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType()));
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

        onAirColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getUploadedStatus()));
        upDownColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getUploadedStatus()));
        deleteColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getUploadedStatus()));
        fileLastModifiedColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()
                .getLastModified()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));

        ObservableList<FileInfo> fileListObserver = dataModel.getFileInfos();
        filesList.getItems().addAll(fileListObserver);
        fileListObserver.addListener((ListChangeListener<FileInfo>) c -> {
            filesList.getItems().clear();
            filesList.getItems().addAll(c.getList());
            updateRows();
            filesList.sort();
        });

        filesList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                FileInfo fileInfo = getSelectedFileInfo();
                if (fileInfo.getFileType().equals("folder")) {
                    updateFilesList(fileInfo);
                }
            }
        });

        updateFilesList(new FileInfo(Paths.get(dataModel.getRootPath())));
    }

    private void updateFilesList(FileInfo fileInfo) {
        clientFileService.updateFileList(fileInfo);
        setAirStatus();
        setDeleteButtons();
        setDownloadUploadButton();
    }

    private void updateRows() {
        setAirStatus();
        setDeleteButtons();
        setDownloadUploadButton();
    }

    private void setAirStatus() {
        onAirColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(UploadedStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
                    CellData data = CellData.cellData(item);
                    Circle circle = new Circle(6);
                    circle.setFill(data.getColor());
                    setGraphic(circle);
                }
            }

            private boolean isSafe(UploadedStatus item, boolean empty) {
                return !empty && Objects.nonNull(item);
            }
        });
    }

    private void setDownloadUploadButton() {
        upDownColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(UploadedStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
                    if (item == UploadedStatus.AIR) {
                        JFXButton button = new JFXButton();
                        button.setText("▼");
                        button.setStyle("-fx-background-color: green; " +
                                "-fx-border-color: aliceblue; " +
                                "-fx-text-fill: aliceblue;" +
                                "-fx-border-radius: 50%;");
                        setGraphic(button);

                        button.setOnMouseClicked(event -> {
                            FileInfo fileInfo = getTableView().getItems().get(getIndex());
                            clientFileService.sendRequestForFileDownloading(fileInfo);
                            updateFilesList(fileInfo);
                        });
                    }

                    if (item == UploadedStatus.NOT_UPLOADED) {
                        JFXButton button = new JFXButton();
                        button.setText("▲");
                        button.setStyle("-fx-background-color: #0C0878; " +
                                "-fx-border-color: aliceblue;  " +
                                "-fx-text-fill: aliceblue; " +
                                "-fx-border-radius: 50%;");
                        setGraphic(button);

                        button.setOnMouseClicked(event -> {
                            FileInfo fileInfo = getTableView().getItems().get(getIndex());
                            clientFileService.sendFileToServer(fileInfo);
                            updateFilesList(fileInfo);
                        });
                    }
                }
            }

            private boolean isSafe(UploadedStatus item, boolean empty) {
                return !empty && Objects.nonNull(item);
            }
        });
    }

    private void setDeleteButtons() {
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(UploadedStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
                    if (item == UploadedStatus.AIR || item == UploadedStatus.UPLOADED) {
                        JFXButton button = new JFXButton();
                        button.setText("x");
                        setGraphic(button);

                        button.setOnMouseClicked(event -> {
                            FileInfo fileInfo = getTableView().getItems().get(getIndex());
                            clientFileService.sendRequestForDeleting(fileInfo);
                        });
                    }
                }
            }

            private boolean isSafe(UploadedStatus item, boolean empty) {
                return !empty && Objects.nonNull(item);
            }
        });
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        if (currentRelativePath.equals("")) {
            return;
        }
        clientFileService.sendFolderUpRequest(currentRelativePath);
    }

    public FileInfo getSelectedFileInfo() {
        return filesList.getSelectionModel().getSelectedItem();
    }

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(mainDialog));
        stage.setTitle("cloud");
        stage.setResizable(false);
        connectionStatusLamp.setFill(Color.GREEN);
        stage.show();
    }

    //удалить локально
    public void deleteFile(ActionEvent actionEvent) {
        clientFileService.deleteFile(getSelectedFileInfo(), currentRelativePath);
    }

    public void addNewFolder(ActionEvent actionEvent) {
        fxWeaver.loadController(FileNameController.class).show();
    }

    public void addNewFile(ActionEvent actionEvent) {
        fxWeaver.loadController(ModalPickFileController.class).show();
    }
}

