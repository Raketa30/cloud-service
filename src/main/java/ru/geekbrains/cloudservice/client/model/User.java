package ru.geekbrains.cloudservice.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private Boolean active;
    private UserRole role;
}
