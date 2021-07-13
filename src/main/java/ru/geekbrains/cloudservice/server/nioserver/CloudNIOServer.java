package ru.geekbrains.cloudservice.server.nioserver;

import ru.geekbrains.cloudservice.server.api.FileWriter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class CloudNIOServer {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private String serverRootPath;

    public CloudNIOServer() {
        this.serverRootPath = "server_directory";

        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(8189));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started; Port 8989...");

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isValid()) {
                        try {
                            if (key.isAcceptable()) {
                                handleAccept();
                            } else if (key.isReadable()) {
                                handleRead(key);
                            }
                            iterator.remove();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileWriter fileWriter = new FileWriter(serverRootPath + "/" + "1.txt");
        buffer.clear();
        fileWriter.write(buffer, 0);
    }

    private void handleAccept() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        System.out.println("Connected " + socketChannel.getRemoteAddress());
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) {
        new CloudNIOServer();
    }
}
