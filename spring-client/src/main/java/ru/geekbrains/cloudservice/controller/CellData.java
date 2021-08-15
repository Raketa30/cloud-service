package ru.geekbrains.cloudservice.controller;

import javafx.scene.paint.Color;
import ru.geekbrains.cloudservice.model.UploadedStatus;

import java.util.Arrays;

public enum CellData {

    HIGH(UploadedStatus.UPLOADED, Color.GREEN),
    MEDIUM(UploadedStatus.AIR, Color.YELLOW),
    NONE(UploadedStatus.NOT_UPLOADED, Color.BLUEVIOLET);

    private final UploadedStatus data;
    private final Color color;

    CellData(UploadedStatus data, Color color) {
        this.data = data;
        this.color = color;
    }

    public static CellData cellData(UploadedStatus data) {
        return Arrays.stream(values())
                .filter(e -> e.data.equals(data))
                .findAny()
                .orElse(NONE);
    }

    public Color getColor() {
        return color;
    }
}
