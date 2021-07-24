package ru.geekbrains.cloudservice.commands;

public interface Response<T, V> {
    V getResponseType();

    T getResponseBody();
}
