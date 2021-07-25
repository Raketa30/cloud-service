package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.service.AuthServerService;
import ru.geekbrains.cloudservice.service.FileServerService;

import java.util.List;

@Slf4j
public class ServerFilesDataHandler extends SimpleChannelInboundHandler<Request<FileInfo, FileOperationRequestType>> {
    private final AuthServerService authServerService;
    private final FileServerService fileServerService;

    public ServerFilesDataHandler(AuthServerService authServerService) {
        this.authServerService = authServerService;
        this.fileServerService = new FileServerService();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("ready for data transer");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel uregistered");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channel read complete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request<FileInfo, FileOperationRequestType> request) throws Exception {
        log.info("reseved file request {}", request.getType());
        fileServerService.setActiveUser(authServerService.getUserFormContext(ctx));
        switch (request.getType()) {
            case SAVE_FILE_REQUEST:
                FileInfo fileInfo = request.getRequestBody();
                ctx.writeAndFlush(fileServerService.checkReceivedFileInfo(fileInfo));
                break;

            case SAVE_FILE:
                fileServerService.saveFile(request.getRequestBody(), ctx);
                break;

            case SAVE_FILES_LIST_REQUEST:
                fileServerService.saveFileList(request.getRequestBody());
                break;

            case DOWNLOAD_FILE:
                FileInfo file = fileServerService.getFile(request.getRequestBody());
                ctx.writeAndFlush(file);
                break;

            case DOWNLOAD_FILE_LIST:
                List<FileInfo> fileInfoList = fileServerService.getFileList(request.getRequestBody());
                break;
        }
    }


}
