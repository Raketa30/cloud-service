package ru.geekbrains.cloudservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.api.ClientHandler;
import ru.geekbrains.cloudservice.commands.RequestMessage;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequest;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.FileInfo;

@Slf4j
@Service
public class FileService {
    private final ClientHandler clientHandler;

    @Autowired
    public FileService(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void sendRequestForFileSaving(FileInfo fileInfo) {
        clientHandler.sendRequestToServer(new RequestMessage(new FileOperationRequest(FileOperationRequestType.SAVE_FILE_REQUEST), fileInfo));
        log.info("sendRequestForFileSaving {}", fileInfo);
    }

    public void sendFileToServer(FileInfo responseBody){
//        try {
//            SocketChannel socketChannel = (SocketChannel) clientHandler.getChannelHandlerContext();
//            FileSender fileSender = new FileSender(socketChannel);
//            FileReader fileReader = new FileReader(fileSender, responseBody.getPath());
//            fileReader.read();
//            clientHandler.sendRequestToServer(new FilesOperationRequest(FileOperationRequestType.SAVE_FILE, responseBody));
//        } catch (IOException e) {
//            log.warn("Problems with file sending");
//        }

    }
}
