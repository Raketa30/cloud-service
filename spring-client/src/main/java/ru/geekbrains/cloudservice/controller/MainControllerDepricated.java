package ru.geekbrains.cloudservice.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import ru.geekbrains.cloudservice.api.NettyConnector;

public class MainControllerDepricated {
    private NettyConnector nettyConnector;

    public MainControllerDepricated() {
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

//    public void sendFileToServerAction(ActionEvent actionEvent) {
//        if (clientPanelController.getSelectedFilename() == null) {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, "ФАЙл не выбран", ButtonType.OK);
//            alert.showAndWait();
//            return;
//        }
//
//        Path path = clientPanelController.getCurrentPath();
//        nettyConnector.send(path);
//    }


}
