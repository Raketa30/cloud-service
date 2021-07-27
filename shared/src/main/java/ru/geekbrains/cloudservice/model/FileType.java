package ru.geekbrains.cloudservice.model;

public enum FileType {
    FILE("file"), DIRECTORY("dir");

    private String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
