package ru.geekbrains.cloudservice.api;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloudservice.service.AuthService;

@Slf4j
public class NettyConnector {
    private AuthHandler authHandler;
    private AuthService authService;

    public NettyConnector(String host, int port) {
        init(host, port);
    }

    private void init(String host, int port) {
        authService = new AuthService();
        authHandler = new AuthHandler();
        new Thread(() -> {
            authService.setAuthHandler(authHandler);
            authHandler.setAuthService(authService);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            Bootstrap bootstrapClient = new Bootstrap();
            bootstrapClient.group(workerGroup);
            bootstrapClient.channel(NioSocketChannel.class);
            bootstrapClient.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrapClient.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            authHandler
                    );
                }
            });

            try {
                ChannelFuture channelFuture = bootstrapClient.connect(host, port).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            workerGroup.shutdownGracefully();
        }).start();
    }

    public AuthHandler getAuthHandler() {
        return authHandler;
    }

    public AuthService getAuthService() {
        return authService;
    }
}
