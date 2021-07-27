package ru.geekbrains.cloudservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Getter
@ToString
public class FileInfo implements AbstractMessage, Serializable {
    private Path path;
    @Setter
    private Path relativePath;
    private String filename;
    private FileType fileType;
    private Long fileSize;
    private LocalDateTime lastModified;
    @Setter
    private String uploadedStatus;

    public FileInfo(Path path) {
        this.path = path;
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
        uploadedStatus = "not";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;
        FileInfo fileInfo = (FileInfo) o;
        return filename.equals(fileInfo.filename) &&
                fileType == fileInfo.fileType &&
                fileSize.equals(fileInfo.fileSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, fileType, fileSize);
    }
}
