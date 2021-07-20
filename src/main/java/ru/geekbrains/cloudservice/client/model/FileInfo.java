package ru.geekbrains.cloudservice.client.model;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@ToString
public class FileInfo implements Serializable {
    private String filename;
    private FileType fileType;
    private Long fileSize;
    private LocalDateTime lastModified;

    public FileInfo(Path path) {
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
