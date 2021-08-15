package ru.geekbrains.cloudservice.commands.impl.files;

import lombok.Data;
import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.dto.FileTO;

@Data
public class FileTransferMessage implements AbstractMessage {
    private final String parent;
    private FileTO fileTO;

    public FileTransferMessage(String parentPath) {
        this.parent = parentPath;
    }
}
