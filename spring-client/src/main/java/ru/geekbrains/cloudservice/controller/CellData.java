package ru.geekbrains.cloudservice.controller;

import javafx.scene.paint.Color;

import java.util.Arrays;

public enum CellData {

    HIGH("yes", Color.GREEN),
    MEDIUM("medium", Color.YELLOW),
    NONE("not", Color.BLUEVIOLET);

    private String data;
    private Color color;

    CellData(String data, Color color) {
        this.data = data;
        this.color = color;
    }

    public static CellData cellData(String data) {
        return Arrays.stream(values())
                .filter(e -> e.data.equals(data))
                .findAny()
                .orElse(NONE);
    }

    public Color getColor() {
        return color;
    }
}
