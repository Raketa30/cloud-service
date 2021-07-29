package ru.geekbrains.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class FileListInfo extends AbstractMessage {
    private final List<FileInfo> fileInfos;
    private String parentPath;
}
