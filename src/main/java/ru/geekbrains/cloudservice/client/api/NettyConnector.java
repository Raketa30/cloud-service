package ru.geekbrains.cloudservice.client.api;

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
import ru.geekbrains.cloudservice.client.api.request.CommandsRequest;

@Slf4j
public class NettyConnector {
    private CommandsRequest commandsRequest;

    public NettyConnector(String host, int port) {
        try {
            init(host, port);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private void init(String host, int port) throws InterruptedException {
        commandsRequest = new CommandsRequest();
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
                        new ClientHandler(),
                        commandsRequest
                );
            }
        });
        ChannelFuture channelFuture = bootstrapClient.connect(host, port).sync();
        channelFuture.channel().closeFuture().sync();
        workerGroup.shutdownGracefully();
    }

    public CommandsRequest getCommandsRequest() {
        return commandsRequest;
    }
}
