package ru.geekbrains.cloudservice.model;

import lombok.*;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paths")
@ToString
public class FileInfo extends AbstractMessage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parentPath;

    private String filePath;

    private String fileType;



    private Long size;

    public FileInfo(String filePath, String fileType, Long size) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;
        FileInfo fileInfo = (FileInfo) o;
        return fileType.equals(fileInfo.fileType) &&
                filePath.equals(fileInfo.filePath) &&
                size.equals(fileInfo.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileType, filePath, size);
    }

}
