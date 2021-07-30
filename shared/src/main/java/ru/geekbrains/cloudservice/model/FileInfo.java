package ru.geekbrains.cloudservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Getter
@Setter
@ToString
public class FileInfo extends AbstractMessage {
    private Path path;
    private Path relativePath;
    private String filename;
    private String fileType;
    private Long fileSize;
    private LocalDateTime lastModified;
    private String uploadedStatus;

    public FileInfo(Path path) {
        this.path = path;
        try {
            this.fileSize = Files.size(path);
            this.filename = path.getFileName().toString();

            if (Files.isRegularFile(path)) {
                fileType = "file";
            } else {
                fileType = "folder";
                this.fileSize = -1L;
            }

            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(),
                    ZoneOffset.ofHours(0));
        } catch (IOException e) {
        }
        uploadedStatus = "not";
    }

    public FileInfo(Path relativePath, String filename, String fileType, Long fileSize, LocalDateTime lastModified) {
        this.relativePath = relativePath;
        this.filename = filename;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;
        FileInfo fileInfo = (FileInfo) o;
        return filename.equals(fileInfo.filename) &&
                        fileType.equals(fileInfo.fileType) &&
                        fileSize.equals(fileInfo.fileSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, fileType, fileSize);
    }
}
