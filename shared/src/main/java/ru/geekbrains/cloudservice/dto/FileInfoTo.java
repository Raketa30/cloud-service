package ru.geekbrains.cloudservice.dto;

import lombok.Data;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class FileInfoTo extends AbstractMessage{
    private String filePath;
    private String fileName;
    private String fileType;
    private LocalDateTime localDateTime;
    private Long size;

    public FileInfoTo(String fileName, String filePath, String fileType, Long size, LocalDateTime localDateTime) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.size = size;
        this.localDateTime = localDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfoTo)) return false;
        FileInfoTo fileInfoTo = (FileInfoTo) o;
        return fileType.equals(fileInfoTo.fileType) &&
                fileName.equals(fileInfoTo.fileName) &&
                filePath.equals(fileInfoTo.filePath) &&
                size.equals(fileInfoTo.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileType, filePath, size);
    }

}
