package ru.geekbrains.cloudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class UserTo implements Serializable {
    private String username;
}
