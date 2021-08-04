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
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.service.ClientAuthService;
import ru.geekbrains.cloudservice.service.ClientFileService;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
@Component
@FxmlView("mainView.fxml")
public class MainController {
    private final FxWeaver fxWeaver;

    private Path currentPath;

    @FXML
    public TextField pathField;
    //Сервисы
    private final ClientAuthService clientAuthService;
    private final ClientFileService clientFileService;

    @FXML
    private BorderPane mainDialog;

    @FXML
    private Stage stage;

    @FXML
    public TableColumn<FileInfo, String> downloadColumn;
    @FXML
    private TableView<FileInfo> filesList;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label freeSpace;

    @FXML
    private JFXButton folderUpButton;

    @FXML
    private TableColumn<FileInfo, Long> fileSizeColumn;

    @FXML
    private TableColumn<FileInfo, String> fileNameColumn;

    @FXML
    private TableColumn<FileInfo, String> fileTypeColumn;

    @FXML
    private TableColumn<FileInfo, String> uploadColumn;

    @FXML
    private TableColumn<FileInfo, String> onAirColumn;

    @FXML
    private TableColumn<FileInfo, String> fileLastModifiedColumn;

    @FXML
    private JFXListView<String> rootFoldersList;

    @Autowired
    public MainController(FxWeaver fxWeaver, ClientAuthService clientAuthService, ClientFileService clientFileService) {
        this.fxWeaver = fxWeaver;
        this.clientAuthService = clientAuthService;
        this.clientFileService = clientFileService;
    }

    @FXML
    private Circle connectionStatusLamp;

    @FXML
    void initialize() {
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

        //кнопки отправки файла/папки в строке
        uploadColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUploadedStatus()));

        //кнопки загрузки файла/папки в строке
        downloadColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getUploadedStatus()));


        try {
            rootFoldersList.getItems().addAll(Files.list(clientAuthService.getUserFolderPath())
                    .filter(path -> new FileInfo(path).getFileType().equals("folder"))
                    .map(s -> new FileInfo(s).getFilename())
                    .collect(Collectors.toList())
            );

        } catch (IOException e) {
            log.warn("Нен удалось отобразить список папок в рутовом каталоге");
        }
        //переходим в папки из рутового каталога
        rootFoldersList.setOnMouseClicked(mouseEvent -> {
            currentPath = clientAuthService.getUserFolderPath().resolve(getSelectedFolder());
            updateList(currentPath);
        });

        fileLastModifiedColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()
                .getLastModified()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));

        filesList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                Path filePath = getCurrentPath();
                if (Files.isDirectory(filePath)) {
                    updateList(filePath);
                }
            }
        });
        currentPath = clientAuthService.getUserFolderPath();
        updateList(currentPath);
        update();
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

    private void setDownloadButton() {
        downloadColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
                    if (item.equals("yes")) {
                        JFXButton button = new JFXButton();
                        button.setText("▼");
                        button.setStyle("-fx-background-color: green; " +
                                "-fx-border-color: aliceblue; " +
                                "-fx-text-fill: aliceblue;" +
                                "-fx-border-radius: 50%;");
                        setGraphic(button);
                    }
                }
            }

            private boolean isSafe(String item, boolean empty) {
                return !empty && Objects.nonNull(item);
            }
        });
    }

    private void setUploadButtons() {
        uploadColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (isSafe(item, empty)) {
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
                            fileInfo.setRelativePath(clientAuthService.getUserFolderPath().relativize(fileInfo.getPath()));

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

    public void updateList(Path path) {
        Path relativizedPath = clientAuthService.getUserFolderPath().relativize(path);
        try {
            pathField.setText("/" + relativizedPath.normalize().toString());
            clientFileService.getFilesList(relativizedPath);
            filesList.getItems().clear();
            if (!clientFileService.getFileListForView().isEmpty()) {
                filesList.getItems().addAll(
                        clientFileService.getFileListForView()
                );
            }
            filesList.sort();
            setAirStatus();
            setUploadButtons();
            setDownloadButton();
        } catch (Exception e) {
            log.warn("updateList ex {}:", e.getMessage());
        }
    }

    private void update() {
        Thread thread = new Thread(() ->
        {
            while (true) {
                updateList(currentPath);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        );
        thread.setDaemon(true);
        thread.start();
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        if (currentPath.equals(clientAuthService.getUserFolderPath())) {
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

    public String getSelectedFolder() {
        return rootFoldersList.getSelectionModel().getSelectedItem();
    }

    public Path getCurrentPath() {
        if (Files.isDirectory(currentPath.resolve(getSelectedFilename()))) {
            currentPath = currentPath.resolve(getSelectedFilename());
        }
        return currentPath;
    }

    public void show() {
        this.stage = new Stage();
        stage.setScene(new Scene(mainDialog));
        stage.setTitle(clientAuthService.getUserTo().getUsername());
        stage.setResizable(false);
        connectionStatusLamp.setFill(Color.GREEN);
        stage.show();
    }

}

