package ru.geekbrains.cloudservice.client.api.nio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileReader {
    private FileChannel fileChannel;
    private NIOFileSender nioFileSender;
    private Path path;

    public FileReader(NIOFileSender nioFileSender, Path path) throws IOException {
        this.nioFileSender = nioFileSender;
        this.path = path;
        this.fileChannel = FileChannel.open(path, StandardOpenOption.READ);
    }


}
