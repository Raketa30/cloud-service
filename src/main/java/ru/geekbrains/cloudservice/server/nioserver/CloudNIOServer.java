package ru.geekbrains.cloudservice.server.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class CloudNIOServer {
    private ServerSocketChannel serverChannel;
    private Selector selector;

    public CloudNIOServer() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(8989));

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

                            } else if (key.isWritable()) {
                                handleWrite(key);
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

    private void handleWrite(SelectionKey key) {

    }

    private void handleRead(SelectionKey key) throws IOException {

    }

    private void handleAccept() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.println("Connected " + socketChannel.getRemoteAddress());
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}
