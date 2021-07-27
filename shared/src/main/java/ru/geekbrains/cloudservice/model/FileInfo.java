package ru.geekbrains.cloudservice.model;

import lombok.*;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paths")
@ToString
public class FileInfo extends AbstractMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileType;

    private String filePath;

    private Long size;

    public FileInfo(String filePath, String fileType, Long size) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.size = size;
    }
}
