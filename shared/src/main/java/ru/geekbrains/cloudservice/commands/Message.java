package ru.geekbrains.cloudservice.commands;

import java.io.Serializable;

public abstract class Message implements Serializable {
    abstract AbstractMessage getAbstractMessageObject();
}
