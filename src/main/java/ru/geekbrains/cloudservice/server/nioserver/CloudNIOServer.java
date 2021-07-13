package ru.geekbrains.cloudservice.server.nioserver;

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
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        System.out.println("Reading from " + socketChannel.getRemoteAddress());

        while (true) {
            int bytesRead = socketChannel.read(buffer);

            if (bytesRead == -1) {
                System.out.println("Connection closed " + socketChannel.getRemoteAddress());
                socketChannel.close();
            }

            if (bytesRead == 0) {
                break;
            }

            if (bytesRead > 0 && buffer.get(buffer.position() - 1) == '\n') {
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }
        }
    }

    private void handleAccept() throws IOException {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.println("Connected " + socketChannel.getRemoteAddress());
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}
