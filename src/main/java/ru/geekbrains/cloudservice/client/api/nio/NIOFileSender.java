package ru.geekbrains.cloudservice.client.api.nio;

import ru.geekbrains.cloudservice.client.api.Sender;
import ru.geekbrains.cloudservice.client.model.FileInfo;
import ru.geekbrains.cloudservice.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

public class NIOFileSender implements Sender {
    private SocketChannel client;

    public NIOFileSender(SocketChannel client) {
        this.client = client;
    }

    @Override
    public void send(Path path) {
        try {
            FileInfo fileInfo = new FileInfo(path);
            client.write(fileInfoObjectToByteBuffer(fileInfo));

            FileReader fileReader = new FileReader(this, path);
            fileReader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer fileInfoObjectToByteBuffer(FileInfo fileInfo) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(fileInfo);
        outputStream.flush();
        return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
    }

    public SocketChannel getClient() {
        return this.client;
    }

    public void transfer(FileChannel fileChannel, long position, long size) throws IOException {
        while(position < size) {
            position += fileChannel.transferTo(position, Constants.MAX_TRANSFER_VALUE, this.client);
        }
    }
}
