package ru.geekbrains.cloudservice.server.nioserver;

import ru.geekbrains.cloudservice.client.model.FileInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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
            serverChannel.socket().bind(new InetSocketAddress(8989));
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
                                handleAccept(key);
                            } else if (key.isReadable()) {
                                handleRead(key);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int numRead = -1;
        numRead = socketChannel.read(byteBuffer);

        if (numRead == -1) {
            Socket socket = socketChannel.socket();
            SocketAddress socketAddress = socket.getRemoteSocketAddress();
            System.out.println("Connection closed " + socketAddress);
            serverChannel.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[numRead];
        System.arraycopy(byteBuffer.array(), 0, data, 0, numRead);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);) {
            FileInfo fileInfo = (FileInfo) objectInputStream.readObject();
            System.out.println(fileInfo);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress socketAddress = socket.getRemoteSocketAddress();
        System.out.println("Connected to :" + socketAddress);
        channel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) {
        new CloudNIOServer();
    }
}
