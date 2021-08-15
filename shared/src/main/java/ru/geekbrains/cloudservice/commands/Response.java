package ru.geekbrains.cloudservice.commands;

import java.io.Serializable;

public interface Response extends Serializable {
    CommandType getResponseType();
}
