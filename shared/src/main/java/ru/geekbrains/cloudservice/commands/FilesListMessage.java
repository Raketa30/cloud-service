package ru.geekbrains.cloudservice.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.cloudservice.dto.FileInfoTo;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class FilesListMessage extends AbstractMessage {
    private List<FileInfoTo> fileInfoTos;
    private String relativePath;

    public FilesListMessage(String relativePath) {
        this.relativePath = relativePath;
    }
}
