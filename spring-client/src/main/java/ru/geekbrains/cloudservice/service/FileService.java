package ru.geekbrains.cloudservice.service;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.api.FileHandler;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.commands.files.FilesOperationRequest;
import ru.geekbrains.cloudservice.controller.MainController;
import ru.geekbrains.cloudservice.model.FileInfo;

@Slf4j
@Component
public class FileService {
    private FileHandler fileHandler;
    private MainController mainController;

    @Autowired
    public FileService(FileHandler fileHandler, MainController mainController) {
        this.fileHandler = fileHandler;
        this.mainController = mainController;
    }

    public void sendRequestForFileSaving(FileInfo fileInfo) {
        FilesOperationRequest operationRequest = new FilesOperationRequest(FileOperationRequestType.SAVE_FILE, fileInfo);
        fileHandler.getChannelHandlerContext().writeAndFlush(operationRequest);
    }

    public void sendFileToServer(FileInfo responseBody) {
        ChannelHandlerContext ctx = fileHandler.getChannelHandlerContext();

    }


}
