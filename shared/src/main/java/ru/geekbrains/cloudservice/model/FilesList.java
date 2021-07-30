package ru.geekbrains.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.dto.FileInfoTo;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class FilesList extends AbstractMessage {
    private List<FileInfoTo> fileInfoTos;
    private String parentPath;

    public FilesList(String parentPath) {
        this.parentPath = parentPath;
    }
}
