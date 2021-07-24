package ru.geekbrains.cloudservice.api;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.commands.Request;
import ru.geekbrains.cloudservice.commands.files.FileOperationRequestType;
import ru.geekbrains.cloudservice.model.FileInfo;
import ru.geekbrains.cloudservice.model.User;
import ru.geekbrains.cloudservice.service.AuthServerService;
import ru.geekbrains.cloudservice.service.FileServerService;

import java.nio.file.Paths;
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User user = authServerService.getUserFormContext(ctx);
        fileServerService.setUserRootPath(Paths.get(user.getServerRootPath()));
        log.info("user {} active", user.getUsername());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request<FileInfo, FileOperationRequestType> request) throws Exception {
        switch (request.getType()) {
            case SAVE_FILE:
                fileServerService.saveFile(request.getRequestBody());
                break;

            case SAVE_FILES_LIST:
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
