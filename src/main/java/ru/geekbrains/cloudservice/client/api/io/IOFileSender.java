package ru.geekbrains.cloudservice.client.api.io;

import ru.geekbrains.cloudservice.client.api.Sender;
import ru.geekbrains.cloudservice.client.model.FileInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOFileSender implements Sender {
    private DataOutputStream outputStream;

    public IOFileSender(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void send(Path path) {
        FileInfo fileInfo = new FileInfo(path);

        new Thread(() -> {
            try {
                outputStream.writeUTF(fileInfo.getFilename());
                outputStream.writeLong(fileInfo.getFileSize());
                Files.copy(path, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
