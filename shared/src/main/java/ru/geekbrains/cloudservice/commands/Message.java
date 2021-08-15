package ru.geekbrains.cloudservice.commands;

import java.io.Serializable;

public interface Message extends Serializable {
    AbstractMessage getAbstractMessageObject();
}
