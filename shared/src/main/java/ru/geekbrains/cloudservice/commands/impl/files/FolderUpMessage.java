package ru.geekbrains.cloudservice.commands.impl.files;

import lombok.Data;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

@Data
public class FolderUpMessage implements AbstractMessage {
    private final String currentPath;
}
