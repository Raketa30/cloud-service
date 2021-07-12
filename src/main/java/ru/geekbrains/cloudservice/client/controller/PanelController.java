package ru.geekbrains.cloudservice.client.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.geekbrains.cloudservice.client.model.FileInfo;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {
    private String panelPath;

    @FXML
    public TextField pathField;

    @FXML
    TableView<FileInfo> filesTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("Type");
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getName()));
        fileTypeColumn.setPrefWidth(50);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        fileTypeColumn.setPrefWidth(200);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getFileSize()));
        fileSizeColumn.setPrefWidth(50);
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
                        text = "[DIR]";
                    }

                    setText(text);
                }
            }
        });

        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Last Modified");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        fileDateColumn.setPrefWidth(100);

        filesTable.getColumns().addAll(fileNameColumn, fileTypeColumn, fileSizeColumn, fileDateColumn);
        filesTable.getSortOrder().addAll(fileTypeColumn, fileNameColumn);

        filesTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                Path path = getCurrentPath();
                updateList(path);
            }
        });

        updateList(Paths.get("/Users/duckpool/dev/courses/Geekbrains/cloud-service"));
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(
                    Files.list(path)
                            .map(FileInfo::new)
                            .collect(Collectors.toList())
            );
            filesTable.sort();
        } catch (IOException e) {
            new Alert(Alert.AlertType.WARNING, "Не удалось отобразить список файлов", ButtonType.OK);
        }

    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upPath = Paths.get(pathField.getText()).getParent();
        if (upPath != null) {
            updateList(upPath);
        }
    }

    public String getSelectedFilename() {
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }

    public void setPanelPath(String panelPath) {
        this.panelPath = panelPath;
    }

    public Path getCurrentPath() {
        return Paths.get(pathField.getText()).resolve(getSelectedFilename());
    }
}
