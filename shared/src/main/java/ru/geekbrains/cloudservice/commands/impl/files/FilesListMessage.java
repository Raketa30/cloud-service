package ru.geekbrains.cloudservice.commands.impl.files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.dto.FileTO;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class FilesListMessage implements AbstractMessage {
    private List<FileTO> fileTOS;
    private String parent;

    public FilesListMessage(String parent) {
        this.parent = parent;
    }
}
