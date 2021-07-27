package ru.geekbrains.cloudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class UserTo implements AbstractMessage, Serializable {
    private String username;
}
