package ru.geekbrains.cloudservice.repo;

import ru.geekbrains.cloudservice.dao.OperationalDBConnection;
import ru.geekbrains.cloudservice.model.FileInfo;

import java.util.Optional;

public class UserOperationalPathsRepo {
    private OperationalDBConnection operationalDBConnection;

    public UserOperationalPathsRepo() {
        this.operationalDBConnection = new OperationalDBConnection();
    }

    public Optional<FileInfo> findFileInfoByRelativePath(String filePath) {
        return operationalDBConnection.findFileByRelativePath(filePath);
    }
}
