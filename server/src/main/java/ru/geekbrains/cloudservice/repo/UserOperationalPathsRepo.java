package ru.geekbrains.cloudservice.repo;

import ru.geekbrains.cloudservice.dao.OperationalDBConnection;
import ru.geekbrains.cloudservice.dto.FileInfoTo;

import java.util.List;
import java.util.Optional;

public class UserOperationalPathsRepo {
    private OperationalDBConnection operationalDBConnection;

    public UserOperationalPathsRepo() {
        this.operationalDBConnection = new OperationalDBConnection();
    }

    public Optional<FileInfoTo> findFileInfoByRelativePath(String filePath) {
        return operationalDBConnection.findFileByRelativePath(filePath);
    }

    public Optional<List<FileInfoTo>> findFilesByParentPath(String filePath) {
        return operationalDBConnection.findFilesByParentPath(filePath);
    }

    public void saveFileInfo(FileInfoTo fileInfoTo) {
        operationalDBConnection.saveFileInfo(fileInfoTo);
    }
}
