package ru.geekbrains.cloudservice.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ru.geekbrains.cloudservice.model.LocalFileInfo;

import java.net.URL;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ServerPanelController implements Initializable {
    @FXML
    public TextField pathField;

    @FXML
    TableView<LocalFileInfo> filesTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<LocalFileInfo, String> fileTypeColumn = new TableColumn<>("Type");
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType()));
        fileTypeColumn.setPrefWidth(50);

        TableColumn<LocalFileInfo, String> fileNameColumn = new TableColumn<>("Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        fileTypeColumn.setPrefWidth(200);

        TableColumn<LocalFileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
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

        TableColumn<LocalFileInfo, String> fileDateColumn = new TableColumn<>("Last Modified");
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


    }

    public void updateList(Path path) {

    }

    public void btnPathUpAction(ActionEvent actionEvent) {

    }

    public String getSelectedFilename() {
        return null;
    }

    public Path getCurrentPath() {
        return null;
    }
}
