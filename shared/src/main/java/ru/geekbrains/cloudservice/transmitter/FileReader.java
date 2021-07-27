package ru.geekbrains.cloudservice.transmitter;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@Slf4j
public class FileReader {
    private final FileChannel fileChannel;
    private final FileSender fileSender;

    public FileReader(FileSender fileSender, Path path) throws IOException {
        if(Objects.isNull(fileSender)) {
            log.warn("sender and path required");
            throw new IllegalArgumentException("sender and path required");
        }
        this.fileSender = fileSender;
        this.fileChannel = FileChannel.open(path, StandardOpenOption.READ);
    }

    public void read() throws IOException {
        try {
            transfer();
        } finally {
            close();
        }
    }

    private void transfer() throws IOException {
        this.fileSender.transfer(this.fileChannel, 0L, this.fileChannel.size());
    }

    public void close() throws IOException {
        this.fileChannel.close();
    }

}
