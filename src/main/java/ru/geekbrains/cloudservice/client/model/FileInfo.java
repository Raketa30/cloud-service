package ru.geekbrains.cloudservice.client.model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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

    @Override
    public String toString() {
        return "FileInfo{" +
                "filename='" + filename + '\'' +
                ", fileType=" + fileType +
                ", fileSize=" + fileSize +
                ", lastModified=" + lastModified +
                '}';
    }
}
