package ru.geekbrains.cloudservice.client.model;

import lombok.Getter;
import lombok.ToString;
import ru.geekbrains.cloudservice.client.service.commands.AbstractCommand;
import ru.geekbrains.cloudservice.client.service.commands.CommandType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@ToString
public class FileMessage extends AbstractCommand {
    private String filename;
    private long fileSize;
    private FileType fileType;
    private LocalDateTime lastModified;

    public FileMessage(Path path) {
        try {
            this.fileSize = Files.size(path);
            this.filename = path.getFileName().toString();

            if (Files.isRegularFile(path)) {
                fileType = FileType.FILE;
            } else {
                fileType = FileType.DIRECTORY;
                this.fileSize = -1L;
            }

            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(),
                    ZoneOffset.ofHours(0));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_UPLOAD_REQUEST;
    }

    public enum FileType {
        FILE("File"), DIRECTORY("Dir");

        private String name;

        FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
