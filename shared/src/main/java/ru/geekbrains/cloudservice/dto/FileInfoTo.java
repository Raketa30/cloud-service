package ru.geekbrains.cloudservice.dto;

import lombok.*;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paths")
@ToString
public class FileInfoTo extends AbstractMessage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "parent_path")
    private String parentPath;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "last_modified")
    private LocalDateTime localDateTime;

    @Column(name = "file_size")
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
