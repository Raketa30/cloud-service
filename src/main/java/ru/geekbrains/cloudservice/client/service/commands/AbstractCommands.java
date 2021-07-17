package ru.geekbrains.cloudservice.client.service.commands;

import java.io.Serializable;


public abstract class AbstractCommands implements Serializable {
    public abstract CommandType getType();
}
