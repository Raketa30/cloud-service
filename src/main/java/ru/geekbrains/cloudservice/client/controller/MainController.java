package ru.geekbrains.cloudservice.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import ru.geekbrains.cloudservice.client.api.NettyConnector;

import java.nio.file.Path;

public class MainController {
    private final NettyConnector nettyConnector;
    @FXML
    public VBox leftPanel;
    @FXML
    public VBox rightPanel;

    public MainController() {
        nettyConnector =  new NettyConnector("localhost", 8989);
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void sendFileToServerAction(ActionEvent actionEvent) {
        ClientPanelController clientPanelController = (ClientPanelController) leftPanel.getProperties().get("ctrl");
        if (clientPanelController.getSelectedFilename() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "ФАЙл не выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Path path = clientPanelController.getCurrentPath();
        nettyConnector.send(path);
    }


}
