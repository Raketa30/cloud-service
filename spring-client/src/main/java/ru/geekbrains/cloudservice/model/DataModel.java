package ru.geekbrains.cloudservice.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.dto.UserTo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DataModel {
    private final ObservableList<FileInfo> fileInfos;
    private final SimpleObjectProperty<UserTo> user;
    private final SimpleStringProperty rootPath;
    private final SimpleStringProperty relativePath;


    public DataModel() {
        this.fileInfos = FXCollections.observableArrayList(new ArrayList<>());
        this.user = new SimpleObjectProperty<>(new UserTo("empty"));
        this.rootPath = new SimpleStringProperty("empty");
        this.relativePath = new SimpleStringProperty("/");

    }

    public ObservableList<FileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<FileInfo> localList) {
        fileInfos.clear();
        this.fileInfos.addAll(localList);
    }

    public UserTo getUser() {
        return user.get();
    }

    public void setUser(UserTo user) {
        this.user.set(user);
    }

    public SimpleObjectProperty<UserTo> userProperty() {
        return user;
    }

    public String getRootPath() {
        return rootPath.get();
    }

    public void setRootPath(String rootPath) {
        this.rootPath.set(rootPath);
    }

    public SimpleStringProperty rootPathProperty() {
        return rootPath;
    }

    public SimpleStringProperty relativePathProperty() {
        return relativePath;
    }

    public String getRelativePath() {
        return relativePath.get();
    }

    public void setRelativePath(Path path) {
        String relative = Paths.get(getRootPath()).relativize(path).toString();
        this.relativePath.set(relative);
    }
}