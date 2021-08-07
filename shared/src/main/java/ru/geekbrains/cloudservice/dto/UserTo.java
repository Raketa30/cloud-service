package ru.geekbrains.cloudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.geekbrains.cloudservice.commands.AbstractMessage;

@AllArgsConstructor
@Getter
public class UserTo extends AbstractMessage {
    private final String username;
}
