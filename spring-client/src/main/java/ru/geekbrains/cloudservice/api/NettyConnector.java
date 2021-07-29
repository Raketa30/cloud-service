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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyConnector {
    private ClientHandler clientHandler;

    public NettyConnector() {
        new Thread(() -> init("localhost", 8189)).start();
    }

    @Autowired
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    private void init(String host, int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrapClient = new Bootstrap();
        bootstrapClient.group(workerGroup);
        bootstrapClient.channel(NioSocketChannel.class);
        bootstrapClient.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrapClient.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("oe", new ObjectEncoder())

                        .addLast("od", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
//                        .addLast("cw", new ChunkedWriteHandler())
                        .addLast("2", clientHandler);
            }
        });
        try {
            ChannelFuture channelFuture = bootstrapClient.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        workerGroup.shutdownGracefully();
    }
}
