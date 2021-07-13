package ru.geekbrains.cloudservice.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import ru.geekbrains.cloudservice.client.api.io.ClientConnector;
import ru.geekbrains.cloudservice.client.api.nio.FileReader;
import ru.geekbrains.cloudservice.client.api.nio.NIOClientConnector;

import java.io.IOException;
import java.nio.file.Path;

public class MainController {
    @FXML
    public VBox leftPanel;
    @FXML
    public VBox rightPanel;
//    private ClientConnector clientConnector;
    private NIOClientConnector clientConnector;

    public MainController() {
//        this.clientConnector = new ClientConnector("localhost", 8989);
       this.clientConnector = new NIOClientConnector(8189);
    }

    public void btnExitAction(ActionEvent actionEvent) {
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
        try {
            FileReader reader = new FileReader(clientConnector, path);
            reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        clientConnector.send(path);
    }

    private void setPanelsLinks(PanelController leftPC, PanelController rightPC) {
        leftPC.setPanelPath("/Users/duckpool/dev");
        rightPC.setPanelPath("server_directory");
    }
}
