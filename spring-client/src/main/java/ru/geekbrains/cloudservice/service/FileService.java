package ru.geekbrains.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.commands.files.FilesOperationRequest;
import ru.geekbrains.cloudservice.commands.transmitter.FileReader;
import ru.geekbrains.cloudservice.commands.transmitter.FileSender;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.io.IOException;
import java.nio.channels.SocketChannel;

@Slf4j
@Service
public class FileService {
    private Cli

    public void sendRequestForFileSaving(FileInfo fileInfo) {
        clientHandler.sendRequestToServer(new FilesOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST, fileInfo));
        log.info("sendRequestForFileSaving {}", fileInfo);
    }

    public void sendFileToServer(FileInfo responseBody) throws IOException {
        SocketChannel socketChannel = (SocketChannel) clientHandler.getChannelHandlerContext();
        FileSender fileSender = new FileSender(socketChannel);
        FileReader fileReader = new FileReader(fileSender, responseBody.getPath());
        fileReader.read();
        clientHandler.sendRequestToServer(new FilesOperationRequest(FileOperationRequestType.SAVE_FILE, responseBody));
    }
}
