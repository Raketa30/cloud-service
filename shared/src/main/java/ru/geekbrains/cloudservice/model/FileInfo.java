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
public class FileInfo implements AbstractMessage {
    private Path path;
    private Path relative;
    private String filename;
    private String type;
    private Long size;
    private LocalDateTime lastModified;
    private UploadedStatus uploadedStatus;

    public FileInfo(Path path) {
        this.path = path;
        try {
            this.size = Files.size(path);
            this.filename = path.getFileName().toString();

            if (Files.isRegularFile(path)) {
                type = "file";
            } else {
                type = "folder";
                this.size = -1L;
            }

            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(),
                    ZoneOffset.ofHours(0));
        } catch (IOException e) {
        }
        uploadedStatus = UploadedStatus.NOT_UPLOADED;
    }

    public FileInfo(Path relative, String filename, String type, Long size, LocalDateTime lastModified) {
        this.relative = relative;
        this.filename = filename;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;
        FileInfo fileInfo = (FileInfo) o;
        return filename.equals(fileInfo.filename) &&
                        type.equals(fileInfo.type) &&
                        size.equals(fileInfo.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, type, size);
    }
}
