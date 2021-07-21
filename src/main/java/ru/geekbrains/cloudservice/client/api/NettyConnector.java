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

import java.nio.file.Path;

@Slf4j
public class NettyConnector {

    public NettyConnector(String host, int port) {
        init(host, port);
    }

    private void init(String host, int port){
        new Thread(() -> {
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
                            new ClientHandler()
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

    public static void main(String[] args) {
        new NettyConnector("localhost", 8989);
    }

    public void send(Path path) {

    }
}
