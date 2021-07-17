package ru.geekbrains.cloudservice.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import ru.geekbrains.cloudservice.client.api.Connector;
import ru.geekbrains.cloudservice.client.api.nio.NIOConnector;

import java.nio.file.Path;

public class MainController {
    @FXML
    public VBox leftPanel;
    @FXML
    public VBox rightPanel;

    private Connector clientConnector;

    public MainController() {
        clientConnector = new NIOConnector("localhost", 8989);
    }

    public void btnExitAction(ActionEvent actionEvent) {
        clientConnector.disconnect();
        Platform.exit();

    }

    public void sendFileToServerAction(ActionEvent actionEvent) {
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "ФАЙл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Path path = leftPC.getCurrentPath();
        clientConnector.send(path);
    }

    private void setPanelsLinks(PanelController leftPC, PanelController rightPC) {
        leftPC.setPanelPath("/Users/duckpool/dev");
        rightPC.setPanelPath("server_directory");
    }
}
