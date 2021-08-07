package ru.geekbrains.cloudservice.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
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
import ru.geekbrains.cloudservice.service.ClientFileService;
import ru.geekbrains.cloudservice.service.FileListViewService;

import java.nio.file.Files;
import java.nio.file.Path;
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
    private final FileListViewService fileListViewService;
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
    public TableColumn<FileInfo, String> deleteColumn;

    @FXML
    private TableColumn<FileInfo, String> onAirColumn;

    @FXML
    private TableColumn<FileInfo, String> upDownColumn;

    @FXML
    private TableColumn<FileInfo, String> fileLastModifiedColumn;

    private Path currentPath;
    private Path root;

    @Autowired
    public MainController(FxWeaver fxWeaver, ClientFileService clientFileService, FileListViewService fileListViewService, DataModel dataModel) {
        this.fxWeaver = fxWeaver;
        this.clientFileService = clientFileService;
        this.fileListViewService = fileListViewService;
        this.dataModel = dataModel;
    }

    @FXML
    private Circle connectionStatusLamp;

    @FXML
    void initialize() {
        SimpleStringProperty relative = dataModel.relativePathProperty();
        pathField.setText(relative.getValue());
        relative.addListener((observable, oldValue, newValue) -> {
            System.out.println(oldValue);
            System.out.println(newValue);
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
        //показываем статус загруженного файл
        //https://stackoverflow.com/questions/42662807/javafx-tablecolumn-cell-change - спасибо ребятам
        onAirColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUploadedStatus()));
        //кнопки отправки/загрузки файла/папки в строке
        upDownColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUploadedStatus()));

        deleteColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));

        fileLastModifiedColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()
                .getLastModified()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));


        ObservableList<FileInfo> fileListObserver = dataModel.getFileInfos();
        filesList.getItems().addAll(fileListObserver);
        fileListObserver.addListener((ListChangeListener<FileInfo>) c -> {
            filesList.getItems().clear();
            filesList.getItems().addAll(c.getList());
            filesList.sort();
        });

        filesList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                Path filePath = getCurrentPath();
                if (Files.isDirectory(filePath)) {
                    updateList(filePath);
                }
            }
        });

        root = Paths.get(dataModel.getRootPath());
        currentPath = root;
        updateList(root);
    }

    private void setAirStatus() {
        onAirColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
                    CellData data = CellData.cellData(item);
                    Circle circle = new Circle(6);
                    circle.setFill(data.getColor());
                    setGraphic(circle);
                }
            }

            private boolean isSafe(String item, boolean empty) {
                return !empty && Objects.nonNull(item);
            }
        });
    }

    private void setDownloadUploadButton() {
        upDownColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
                    if (item.equals("air")) {
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
                        });
                    }

                    if (item.equals("not")) {
                        JFXButton button = new JFXButton();
                        button.setText("▲");
                        button.setStyle("-fx-background-color: #0C0878; " +
                                "-fx-border-color: aliceblue;  " +
                                "-fx-text-fill: aliceblue; " +
                                "-fx-border-radius: 50%;");
                        setGraphic(button);

                        button.setOnMouseClicked(event -> {
                            FileInfo fileInfo = getTableView().getItems().get(getIndex());
                            fileInfo.setRelativePath(root.relativize(fileInfo.getPath()));
                            clientFileService.sendRequestForFileSaving(fileInfo);
                        });
                    }
                }
            }

            private boolean isSafe(String item, boolean empty) {
                return !empty && Objects.nonNull(item);
            }
        });
    }

    private void setDeleteButtons() {
        deleteColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
                    JFXButton button = new JFXButton();
                    button.setText("x");
                    setGraphic(button);

                    button.setOnMouseClicked(event -> {
                        FileInfo fileInfo = getTableView().getItems().get(getIndex());
                        fileInfo.setRelativePath(root.relativize(fileInfo.getPath()));
                        clientFileService.sendRequestForDeleting(fileInfo);
                    });
                }
            }

            private boolean isSafe(String item, boolean empty) {
                return !empty && Objects.nonNull(item);
            }
        });
    }

    public void updateList(Path path) {
        try {
            fileListViewService.updateListView(path);
            clientFileService.updateFileList(path);
            setAirStatus();
            setDeleteButtons();
            setDownloadUploadButton();
        } catch (Exception e) {
            log.warn("updateList ex {}:", e.getMessage());
        }
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        if (currentPath.equals(root)) {
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

    public Path getCurrentPath() {
        if (Files.isDirectory(currentPath.resolve(getSelectedFilename()))) {
            currentPath = currentPath.resolve(getSelectedFilename());
        }
        return currentPath;
    }

    public void show() {
        Platform.runLater(() -> {
            this.stage = new Stage();
            stage.setScene(new Scene(mainDialog));
            stage.setTitle("cloud");
            stage.setResizable(false);
            connectionStatusLamp.setFill(Color.GREEN);
            stage.show();
        });
    }

    //удалить локально
    public void deleteFile(ActionEvent actionEvent) {
        clientFileService.deleteLocalFile(getCurrentPath());
    }

    public void addNewFolder(ActionEvent actionEvent) {
        fxWeaver.loadController(EnterNameController.class).show();
    }

    public void addNewFile(ActionEvent actionEvent) {
        fxWeaver.loadController(ModalPickFileController.class).show();
    }


    public Path getPath() {
        return this.currentPath;
    }
}

