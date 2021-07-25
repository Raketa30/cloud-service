package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.commands.files.FilesOperationRequest;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.commands.transmitter.FileReader;
import ru.geekbrains.cloudservice.commands.transmitter.FileSender;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.service.FileService;

import java.io.IOException;
import java.nio.channels.SocketChannel;

@Slf4j
@Service
@ChannelHandler.Sharable
public class FileHandler extends SimpleChannelInboundHandler<Response<FileInfo, FilesOperationResponseType>> {
    @Autowired
    private FileService fileService;

    private ChannelHandlerContext channelHandlerContext;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channelHandlerContext = ctx;
    }

    public void sendFileOperationRequest(FilesOperationRequest request) {
        channelHandlerContext.writeAndFlush(request);
    }

    public FilesOperationRequest sendFileToServer(FileInfo responseBody) throws IOException {
        SocketChannel socketChannel = (SocketChannel)channelHandlerContext;
        FileSender fileSender = new FileSender(socketChannel);
        FileReader fileReader = new FileReader(fileSender, responseBody.getPath());
        fileReader.read();
        return new FilesOperationRequest(FileOperationRequestType.SAVE_FILE, responseBody);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response<FileInfo, FilesOperationResponseType> response) throws Exception {
        switch (response.getResponseType()) {
            case FILE_READY_TO_SAVE:
                FileInfo responseBody = response.getResponseBody();
                sendFileToServer(responseBody);
                break;
            case FILE_ALREADY_EXIST:

                break;
            case FILE_SAVED:
                break;
            case FILES_LIST_SAVED:
                break;
            case FILE_SENT:
                break;
            case FILE_LIST_SENT:
                break;
            case FILE_NOT_EXIST:
                break;
            case DIRECTORY_NOT_EXIST:
                break;
        }
    }
}
