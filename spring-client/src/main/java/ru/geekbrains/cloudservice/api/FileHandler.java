package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.files.FilesOperationResponseType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.service.FileService;

@Slf4j
@Component
@ChannelHandler.Sharable
public class FileHandler extends SimpleChannelInboundHandler<Response<FileInfo, FilesOperationResponseType>> {
    private ChannelHandlerContext channelHandlerContext;

    @Autowired
    private FileService fileService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("received ctx for file transfer");
        this.channelHandlerContext = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response<FileInfo, FilesOperationResponseType> response) throws Exception {
        switch (response.getResponseType()) {
            case FILE_READY_TO_SAVE:
                FileInfo responseBody = response.getResponseBody();
                fileService.sendFileToServer(responseBody);
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



    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
