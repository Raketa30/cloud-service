package ru.geekbrains.cloudservice.dto;

import lombok.Data;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class FileTO implements AbstractMessage{
    private String relativePath;
    private String filename;
    private String type;
    private LocalDateTime lastMod;
    private Long size;

    public FileTO(String filename, String relativePath, String type, Long size, LocalDateTime lastMod) {
        this.filename = filename;
        this.relativePath = relativePath;
        this.type = type;
        this.size = size;
        this.lastMod = lastMod;
    }

    public FileTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileTO)) return false;
        FileTO fileTO = (FileTO) o;
        return type.equals(fileTO.type) &&
                filename.equals(fileTO.filename) &&
                relativePath.equals(fileTO.relativePath) &&
                size.equals(fileTO.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, relativePath, size);
    }

}
