package ru.geekbrains.cloudservice.commands;

import java.io.Serializable;

public interface Request extends Serializable {
   CommandType getRequestCommandType();
}
