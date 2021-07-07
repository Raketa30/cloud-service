package ru.geekbrains.cloudservice.server.entity;

import java.io.File;

public class User {
    private String name;
    private String linkToFolder;

    public User(String name) {
        this.name = name;
        createFolder(name);
    }

    private void createFolder(String name) {
        File folder = new File(name);
        if(!folder.exists()) {
            folder.mkdir();
            linkToFolder = folder.getAbsolutePath();
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkToFolder() {
        return linkToFolder;
    }

    public void setLinkToFolder(String linkToFolder) {
        this.linkToFolder = linkToFolder;
    }
}
