package ru.geekbrains.cloudservice.commands;

public interface Request<T, V> {
   V getType();

   T getRequestBody();
}
